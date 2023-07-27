-- Password: 00000000, BCrypt: $2a$10$9EkILTFIG/shuQcNzK/Zx.Ns6N.LPfy52qbnzyrSymHvXMW12OIxO
-- Password: 11111111, BCrypt: $2a$10$3ANjDb7flRoeBmwzEgp3duimiCXZB4X/v.fSQrYWPHgNO1Clt1QOK
-- Password: 22222222, BCrypt: $2a$10$s28dvh28x1ANrE7yvZ.UguYNJZ6wXIHcwJxQVsHgzmsDSstZo6/Se
-- Password: 33333333, BCrypt: $2a$10$ukVSvFBoEpcC.zCOi3HPPeHtaGNr.rUlKC8pNEhu6OyPeq3akIO.e
-- Password: 44444444, BCrypt: $2a$10$KU95wNd8p9Ax1iP9RJIPNexD1faQ7jF2xJnNsY1DighFxElCOTW2S
-- Password: 55555555, BCrypt: $2a$10$GADyypVOwzpC2JTMC0dfNO9Dx.XlD8H.e5jva103s2iCH1xF8gmV2
-- Password: 66666666, BCrypt: $2a$10$2Yz0BYX41kHzywF8AFKGhuUzFC4icv3hxHm4zrkIDoinhb3Mh3BpW
-- Password: 77777777, BCrypt: $2a$10$nc65NoMlr3ILUE7d/7OLe.y8jWflJIMSb.QcGivDxcKbyIXeugB/O
-- Password: 88888888, BCrypt: $2a$10$psps81mwR5eSVqZGotRGCO9hEIspkCZR2jLaVh9.NebkKVY6uYxyG
-- Password: 99999999, BCrypt: $2a$10$zqgaCzzKV/AD2R12TQ1GHOSlNJ2Du9ULcK9PsPT5o11cgl97CcjPO

INSERT INTO socialnet.account (
     id,
     is_deleted,
     first_name,
     last_name,
     email,
     password,
     phone,
     photo,
     profile_cover,
     about,
     city,
     country,
     reg_date,
     birth_date,
     message_permission,
     last_online_time,
     is_online,
     is_blocked,
     created_on,
     updated_on
)
VALUES
    ('a0000000-0000-0000-0000-000000000000', -- id
     FALSE, -- is_deleted
     'Seth', -- first_name
     'Green', -- last_name
     'user1000@test.com', -- email
     '$2a$10$9EkILTFIG/shuQcNzK/Zx.Ns6N.LPfy52qbnzyrSymHvXMW12OIxO', -- password
     '+19000001000', -- phone
     'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000001.jpg', -- photo
     'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000011.jpg', -- profile_cover
     'Ciao, I''m Seth Green, a historian from Rome, Italy. I''m fascinated by the past.', -- about
     'Rome', -- city
     'Italy', -- country
     CURRENT_TIMESTAMP, -- reg_date
     '2000-01-01', -- birth_date
     TRUE, -- message_permission
     CURRENT_TIMESTAMP, -- last_online_time
     FALSE, -- is_online
     FALSE, -- is_blocked
     CURRENT_TIMESTAMP, -- created_on
     CURRENT_TIMESTAMP), -- updated_on
    ('a1000000-0000-0000-0000-000000000000', FALSE, 'Seth', 'Macfarlane', 'user1001@example.com', '$2a$10$3ANjDb7flRoeBmwzEgp3duimiCXZB4X/v.fSQrYWPHgNO1Clt1QOK', '+79000001001', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000002.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000012.jpg', 'Hi, I''m Seth Macfarlane, a scriptwriter from Boston, USA. Love crafting stories and humor!', 'Boston', 'United States', CURRENT_TIMESTAMP, '2000-01-02', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a2000000-0000-0000-0000-000000000000', FALSE, 'Irene', 'Merryweather', 'user1002@example.com', '$2a$10$s28dvh28x1ANrE7yvZ.UguYNJZ6wXIHcwJxQVsHgzmsDSstZo6/Se', '+79000001002', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000003.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000013.jpg', 'Hello, I''m Irene Merryweather, a journalist from London. Passionate about sharing the truth.', 'London', 'United Kingdom', CURRENT_TIMESTAMP, '2000-01-03', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a3000000-0000-0000-0000-000000000000', FALSE, 'Peter', 'Rasputin', 'user1003@example.com', '$2a$10$ukVSvFBoEpcC.zCOi3HPPeHtaGNr.rUlKC8pNEhu6OyPeq3akIO.e', '+79000001003', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000004.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000014.jpg', 'Peter Rasputin here, a farmer from Moscow, Russia. I cherish simple living and nature.', 'Moscow', 'Russia', CURRENT_TIMESTAMP, '2000-01-04', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a4000000-0000-0000-0000-000000000000', FALSE, 'Russell', 'Collins', 'user1004@example.com', '$2a$10$KU95wNd8p9Ax1iP9RJIPNexD1faQ7jF2xJnNsY1DighFxElCOTW2S', '+79000001004', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000005.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000015.jpg', 'Russell Collins, a firefighter from Sydney, Australia. Dedicated to helping others.', 'Sydney', 'Australia', CURRENT_TIMESTAMP, '2000-01-05', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a5000000-0000-0000-0000-000000000000', FALSE, 'Neena', 'Thurman', 'user1005@example.com', '$2a$10$GADyypVOwzpC2JTMC0dfNO9Dx.XlD8H.e5jva103s2iCH1xF8gmV2', '+79000001005', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000006.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000016.jpg', 'I''m Neena Thurman, a yoga instructor from Toronto, Canada. I believe in balance and harmony.', 'Toronto', 'Canada', CURRENT_TIMESTAMP, '2000-01-06', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a6000000-0000-0000-0000-000000000000', FALSE, 'Garrison', 'Kane', 'user1006@example.com', '$2a$10$2Yz0BYX41kHzywF8AFKGhuUzFC4icv3hxHm4zrkIDoinhb3Mh3BpW', '+79000001006', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000007.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000017.jpg', 'Garrison Kane, a software developer from Berlin, Germany. Coding is my second language.', 'Berlin', 'Germany', CURRENT_TIMESTAMP, '2000-01-07', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a7000000-0000-0000-0000-000000000000', FALSE, 'Ella', 'Whitby', 'user1007@example.com', '$2a$10$nc65NoMlr3ILUE7d/7OLe.y8jWflJIMSb.QcGivDxcKbyIXeugB/O', '+79000001007', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000008.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000018.jpg', 'Hola, I''m Ella Whitby, a chef from Barcelona, Spain. I love cooking up a storm in the kitchen!', 'Barcelona', 'Spain', CURRENT_TIMESTAMP, '2000-01-08', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a8000000-0000-0000-0000-000000000000', FALSE, 'Vanessa', 'Carlysle', 'user1008@example.com', '$2a$10$psps81mwR5eSVqZGotRGCO9hEIspkCZR2jLaVh9.NebkKVY6uYxyG', '+79000001008', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000009.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000019.jpg', 'Vanessa Carlysle here, a fashion designer from Tokyo, Japan. Passionate about colors and fabrics.', 'Tokyo', 'Japan', CURRENT_TIMESTAMP, '2000-01-09', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a9000000-0000-0000-0000-000000000000', FALSE, 'Wade', 'Wilson', 'user1009@example.com', '$2a$10$zqgaCzzKV/AD2R12TQ1GHOSlNJ2Du9ULcK9PsPT5o11cgl97CcjPO', '+79000001009', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000010.jpg', 'https://storage.yandexcloud.net/team38bucket/00000000-0000-0000-0000-000000000020.jpg', 'Hey, it''s your favorite merc with a mouth, Wade Wilson, coming at you from Paris, France. Expert in chimichanga consumption, breaking the fourth wall, and generally making a nuisance of myself. Remember, maximum effort!', 'Paris', 'France', CURRENT_TIMESTAMP, '2000-01-10', TRUE, CURRENT_TIMESTAMP, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
