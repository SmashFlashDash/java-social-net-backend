    package ru.team38.userservice.services;

    import jakarta.annotation.PostConstruct;
    import lombok.NoArgsConstructor;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.poi.ss.usermodel.*;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.scheduling.annotation.EnableScheduling;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Component;
    import org.springframework.stereotype.Service;
    import ru.team38.common.dto.CityDto;
    import ru.team38.common.dto.CountryDto;
    import java.io.*;
    import java.net.URL;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.StandardCopyOption;
    import java.sql.*;
    import java.time.Instant;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
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
        @Value("${geoService.archivePath}")
        private String archivePath;
        @Value("${geoService.excelPath}")
        private String excelPath;
        @Value("${geoService.folderPath}")
        private String folderPath;

        @PostConstruct
        @Scheduled(cron = "0 0 0 1 */6 ?")
        public void loadGeoData() {
            if (isFileDownloadedAndUsed()) {
                log.info("Обновление файла Excel не требуется");
                return;
            }
            try {
                log.info("База данных стран устарела или отсутствует, запуск обновления");
                deleteFile(excelPath);
                createFolder(folderPath);
                downloadFile(targetSiteUrl, archivePath);
                extractExcelFromArchive(archivePath, excelPath);
                deleteFile(archivePath);
                log.info("Файл Excel успешно сохранен по пути: {}", excelPath);
            } catch (IOException e) {
                log.error("Ошибка при выполнении загрузки геоданных", e);
                return;
            }
            clearCountriesTable();
            clearCitiesTable();
            parseExcelAndSaveData();
        }

        public boolean isFileDownloadedAndUsed() {
            File file = new File(excelPath);
            if (!file.exists()) {
                return false;
            }
            long lastDownloadTime = file.lastModified();
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime sixMonthsAgo = currentTime.minusMonths(6);
            boolean isDownloadedAndUsed = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastDownloadTime),
                    ZoneId.systemDefault()).isAfter(sixMonthsAgo);
            if (isDownloadedAndUsed) {
                log.info("Обновление баз данных стран не требуется т.к прошло менее пол года");
            }
            return isDownloadedAndUsed;
        }

        private void createFolder(String folderPath) throws IOException {
        Path folder = Path.of(folderPath);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
    }

    private void downloadFile(String url, String filePath) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream out = new FileOutputStream(filePath);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(out)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                bufferedOut.write(buffer, 0, bytesRead);
            }
        }
    }

    private void extractExcelFromArchive(String archivePath, String excelPath) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(Files
                .newInputStream(Path.of(archivePath))))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".xlsx")) {
                    Files.copy(zipInputStream, Path.of(excelPath), StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
            }
        }
    }

    private void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Path.of(filePath));
    }

        private void clearCountriesTable() {
            geoRepository.clearCountriesTable();
        }

        private void clearCitiesTable() {
            geoRepository.clearCitiesTable();
        }

        private void parseExcelAndSaveData() {
            try (FileInputStream fis = new FileInputStream(excelPath);
                 Workbook workbook = WorkbookFactory.create(fis)) {
                Sheet countriesSheet = workbook.getSheet("Sheet1");
                List<CountryDto> countries = parseCountriesSheet(countriesSheet);
                saveCountriesToDatabase(countries);
                Sheet citiesSheet = workbook.getSheet("Sheet1");
                List<CityDto> cities = parseCitiesSheet(citiesSheet, countries);
                saveCitiesToDatabase(cities);
            } catch (Exception e) {
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
        long country_id = 0;
        for (String title : uniqueTitles) {
            boolean isDeleted = false;
            CountryDto country = CountryDto.builder()
                    .id(country_id++)
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
                    CityDto city = CityDto.builder()
                            .id(id++)
                            .title(title)
                            .isDeleted(isDeleted)
                            .countryId(countryId)
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