    package ru.team38.userservice.services;

    import jakarta.annotation.PostConstruct;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.poi.ss.usermodel.*;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.scheduling.annotation.EnableScheduling;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Component;
    import org.springframework.stereotype.Service;
    import ru.team38.common.dto.geography.CityDto;
    import ru.team38.common.dto.geography.CountryDto;
    import java.io.*;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.*;
    import java.util.concurrent.atomic.AtomicLong;
    import java.util.stream.Collectors;
    import java.util.zip.ZipEntry;
    import java.util.zip.ZipInputStream;

    import ru.team38.userservice.data.repositories.GeoRepository;

    @Service
    @Slf4j
    @EnableScheduling
    @Component
    @RequiredArgsConstructor
    public class GeoService {
        private final GeoRepository geoRepository;
        @Value("${geoService.urlData}")
        private String targetSiteUrl;

        @PostConstruct
        @Scheduled(cron = "0 0 0 1 */6 ?")
        public void loadGeoData() {
            if (geoRepository.areTablesEmpty()) {
                log.info("Таблицы геоданных пустые. Начинается заполнение...");
                clearCountriesTable();
                clearCitiesTable();
                parseExcelAndSaveData();
            } else {
                log.info("Таблицы геоданных уже содержат данные. Пропускается загрузка новых данных.");
            }
        }

        private void clearCountriesTable() {
            geoRepository.clearCountriesTable();
        }

        private void clearCitiesTable() {
            geoRepository.clearCitiesTable();
        }

        private void parseExcelAndSaveData() {
            try {
                URL url = new URL(targetSiteUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream archiveInputStream = connection.getInputStream();
                    try (ZipInputStream zipInputStream = new ZipInputStream(archiveInputStream)) {
                        ZipEntry entry;
                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            if (entry.getName().equals("worldcities.xlsx")) {
                                Workbook workbook = new XSSFWorkbook(zipInputStream);
                                Sheet countriesSheet = workbook.getSheet("Sheet1");
                                List<CountryDto> countries = parseCountriesSheet(countriesSheet);
                                saveCountriesToDatabase(countries);
                                Sheet citiesSheet = workbook.getSheet("Sheet1");
                                List<CityDto> cities = parseCitiesSheet(citiesSheet, countries);
                                saveCitiesToDatabase(cities);
                                workbook.close();
                                break;
                            }
                        }
                    }
                } else {
                    log.error("Ошибка при получении архива. Код ответа: {}", responseCode);
                }
                connection.disconnect();
            } catch (IOException e) {
                log.error("Ошибка при обработке файла Excel и сохранении данных", e);
            }
        }

            private List<CountryDto> parseCountriesSheet(Sheet countriesSheet) {
        List<CountryDto> countries = new ArrayList<>();
        TreeSet<String> uniqueTitles = new TreeSet<>();
        boolean skipRow = false;
        for (Row row : countriesSheet) {
            if (skipRow) {
                skipRow = false;
                continue;
            }
            Cell cell = row.getCell(1);
            if (cell != null && cell.getCellType() == CellType
                    .STRING && cell.getStringCellValue().equals("city_ascii")) {
                skipRow = true;
                continue;
            }
            String title = row.getCell(4).getStringCellValue();
            uniqueTitles.add(title);
        }
        long countryId = 0;
        for (String title : uniqueTitles) {
            boolean isDeleted = false;
            CountryDto country = CountryDto.builder()
                    .id(countryId++)
                    .title(title)
                    .isDeleted(isDeleted)
                    .build();
            countries.add(country);
        }
        return countries;
    }

        private void saveCountriesToDatabase(List<CountryDto> countries) {
            geoRepository.saveCountry(countries);
        }

        private List<CityDto> parseCitiesSheet(Sheet citiesSheet, List<CountryDto> countries) {
            Set<CityDto> uniqueCities = new TreeSet<>(Comparator.comparingLong(CityDto::getId));
            long id = 0;
            for (Row row : citiesSheet) {
                String title = row.getCell(1).getStringCellValue();
                boolean isDeleted = false;
                String countryTitle = row.getCell(4).getStringCellValue();
                if (title.equals("city_ascii")) {
                    continue;
                }
                Long countryId = null;
                for (CountryDto country : countries) {
                    if (country.getTitle().equals(countryTitle)) {
                        countryId = country.getId();
                        break;
                    }
                }
                if (countryId != null) {
                    double latitude = row.getCell(2).getNumericCellValue();
                    double longitude = row.getCell(3).getNumericCellValue();
                    CityDto city = CityDto.builder()
                            .id(id++)
                            .title(title)
                            .isDeleted(isDeleted)
                            .countryId(countryId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();
                    uniqueCities.add(city);
                }
            }
            return uniqueCities.stream().sorted(Comparator.comparing(CityDto::getTitle)).collect(Collectors.toList());
        }

        private  void saveCitiesToDatabase(List<CityDto> cities) {
            geoRepository.saveCity(cities);
        }

    public ResponseEntity<List<CountryDto>> getCountries() {
        List<CountryDto> countries = geoRepository.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    public List<CityDto> getCitiesByCountryId(String countryId) {
        List<CityDto> cityDtos = new ArrayList<>();
        CountryDto country = geoRepository.getCountryById(Long.valueOf(countryId));
        if (country != null) {
            List<String> cityList = country.getCities();
            AtomicLong idCounter = new AtomicLong(1);
            cityDtos = cityList.stream()
                    .map(city -> {
                        CityDto cityDto = CityDto.builder()
                                .id(idCounter.getAndIncrement())
                                .isDeleted(false)
                                .title(city)
                                .countryId(Long.valueOf(countryId))
                                .build();
                        return cityDto;
                    })
                    .collect(Collectors.toList());
        }
        return cityDtos;
        }
    }