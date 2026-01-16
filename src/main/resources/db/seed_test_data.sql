-- =================================================================================
-- KOMPLETNY SEEDER DANYCH - APLIKACJA DOG WALKING
-- Stan symulacji: 2026-01-02
-- Logika slotów: 30 minut
-- =================================================================================

-- 0. CZYSZCZENIE BAZY DANYCH (TRUNCATE)
-- ---------------------------------------------------------------------------------
TRUNCATE
    public."breeds",
    public."users",
    public."wallets",
    public."addresses",
    public."dogs",
    public."dog_photos",
    public."offers",
    public."reservations",
    public."payments",
    public."transactions",
    public."chats",
    public."chat_messages",
    public."gps_sessions",
    public."gps_points",
    public."reviews",
    public."domain_events",
    public."availability_slots"
RESTART IDENTITY CASCADE;

-- 1. SŁOWNIKI (BREEDS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."breeds" ("breed_code", "name") VALUES
                                                       ('LAB', 'Labrador Retriever'), ('GSD', 'Owczarek Niemiecki'),
                                                       ('BEA', 'Beagle'),             ('BUL', 'Buldog Francuski'),
                                                       ('MIX', 'Mieszaniec'),          ('HUS', 'Husky Syberyjski'),
                                                       ('YOR', 'Yorkshire Terrier'),   ('COR', 'Welsh Corgi Pembroke'),
                                                       ('BOX', 'Bokser'),              ('GLD', 'Golden Retriever');

-- 2. UŻYTKOWNICY (USERS)
-- Hasło dla wszystkich: 'password'
-- ---------------------------------------------------------------------------------
INSERT INTO public."users" ("user_id", "email", "first_name", "last_name", "password_hash", "username", "role", "is_active", "created_at") VALUES
                                        (1, 'admin@homelab.pl', 'Admin', 'Sysop', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'admin', 'ADMIN', TRUE, '2025-01-01 10:00:00+01'),
                                        (2, 'anna.nowak@email.pl', 'Anna', 'Nowak', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'annan', 'USER', TRUE, '2025-01-02 11:30:00+01'),
                                        (3, 'piotr.kowalski@email.pl', 'Piotr', 'Kowalski', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'piotrk', 'USER', TRUE, '2025-01-03 15:45:00+01'),
                                        (4, 'jan.wisniewski@email.pl', 'Jan', 'Wiśniewski', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'janw', 'USER', TRUE, '2025-01-04 09:15:00+01'),
                                        (5, 'kasia.wojcik@email.pl', 'Katarzyna', 'Wójcik', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'kasiaw', 'USER', TRUE, '2025-02-15 09:00:00+01'),
                                        (6, 'michal.lewandowski@email.pl', 'Michał', 'Lewandowski', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'michall', 'USER', TRUE, '2025-03-10 14:20:00+01'),
                                        (7, 'tomek.kaczmarek@email.pl', 'Tomasz', 'Kaczmarek', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'tomekk', 'USER', TRUE, '2025-06-01 12:00:00+01'),
                                        (8, 'zofia.mazur@email.pl', 'Zofia', 'Mazur', '$2a$10$rQ9pEgN2IsyDXiRsNrsN0OJMairrtFSZ96rdx50BRGirUut0KGAg6', 'zofiam', 'USER', TRUE, '2025-05-20 18:00:00+01');

-- 3. PORTFELE (WALLETS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."wallets" ("wallet_id", "user_id", "balance_cents", "currency", "created_at", "version") VALUES
         (1, 1, 0, 'PLN', '2025-01-01 10:00:00+01', 1),
         (2, 2, 34000, 'PLN', '2025-01-02 11:30:00+01', 5),
         (3, 3, 28500, 'PLN', '2025-01-03 15:45:00+01', 5),
         (4, 4, 11500, 'PLN', '2025-01-04 09:15:00+01', 5),
         (5, 5, 15500, 'PLN', '2025-02-15 09:00:00+01', 5),
         (6, 6, 2000, 'PLN', '2025-03-10 14:20:00+01', 2),
         (7, 7, 15000, 'PLN', '2025-06-01 12:00:00+01', 3),
         (8, 8, 26500, 'PLN', '2025-05-20 18:00:00+01', 3);

-- 4. ADRESY (ADDRESSES)
-- ---------------------------------------------------------------------------------
INSERT INTO public."addresses" ("address_id", "user_id", "city", "street", "home_number", "zipcode", "label", "latitude", "longitude", "created_at") VALUES
                                                  (1, 2, 'Warszawa', 'Marszałkowska', '10', '00-001', 'Dom', 52.2297, 21.0122, '2025-01-02 12:00:00+01'),
                                                  (2, 3, 'Warszawa', 'Złota', '44', '00-020', 'Biuro', 52.2318, 21.0060, '2025-01-03 16:00:00+01'),
                                                  (3, 5, 'Warszawa', 'Mickiewicza', '15', '01-517', 'Dom', 52.2680, 20.9850, '2025-02-15 09:10:00+01'),
                                                  (4, 6, 'Warszawa', 'KEN', '20', '02-797', 'Mieszkanie', 52.1400, 21.0500, '2025-03-10 14:30:00+01'),
                                                  (5, 7, 'Warszawa', 'Targowa', '12', '03-731', 'Dom', 52.2490, 21.0400, '2025-06-01 12:10:00+01'),
                                                  (6, 8, 'Warszawa', 'Górczewska', '124', '01-460', 'Dom', 52.2390, 20.9300, '2025-05-20 18:10:00+01');

-- 5. PSY I ZDJĘCIA (DOGS & PHOTOS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."dogs" ("dog_id", "owner_id", "name", "breed_code", "size", "weight_kg", "notes", "is_active", "created_at") VALUES
                             (1, 2, 'Burek', 'MIX', 'MEDIUM', 15.5, 'Lubi gonić rowery, boi się burzy.', TRUE, '2025-01-02 12:30:00+01'),
                             (2, 2, 'Luna', 'LAB', 'LARGE', 28.0, 'Bardzo przyjazna, alergia na drób.', TRUE, '2025-01-02 12:35:00+01'),
                             (3, 4, 'Rex', 'GSD', 'LARGE', 35.0, 'Wymaga silnej ręki.', TRUE, '2025-01-04 10:00:00+01'),
                             (4, 6, 'Fafik', 'YOR', 'SMALL', 3.5, 'Szczeka na duże psy.', TRUE, '2025-03-11 09:00:00+01'),
                             (5, 8, 'Ares', 'HUS', 'LARGE', 26.0, 'Ma mnóstwo energii, potrzebuje biegania.', TRUE, '2025-05-21 10:00:00+01'),
                             (6, 8, 'Daisy', 'GLD', 'LARGE', 30.0, 'Kocha wszystkich ludzi.', TRUE, '2025-05-21 10:05:00+01');

INSERT INTO public."dog_photos" ("photo_id", "dog_id", "url", "uploaded_at") VALUES
(1, 1, '/photos/dogs/1/burek.jpg', '2025-01-05 10:00:00+01'),
(2, 2, '/photos/dogs/2/luna.jpg', '2025-01-05 10:05:00+01'),
(3, 4, '/photos/dogs/4/fafik.jpeg', '2025-03-11 09:05:00+01'),
(4, 5, '/photos/dogs/5/ares.jpg', '2025-05-21 10:10:00+01');

-- 6. OFERTY (OFFERS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."offers" ("offer_id", "walker_id", "description", "price_cents", "is_active", "created_at") VALUES
(1, 3, 'Długie spacery po parkach na Mokotowie i w Centrum', 4500, TRUE, '2025-01-03 17:00:00+01'),
(2, 5, 'Spacery Żoliborz / Bielany - małe psy mile widziane', 3500, TRUE, '2025-02-15 10:00:00+01'),
(3, 7, 'Praga Północ i Południe - spacery z bieganiem', 5000, TRUE, '2025-06-01 13:00:00+01');

-- 7. REZERWACJE (RESERVATIONS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."reservations" ("reservation_id", "owner_id", "walker_id", "dog_id", "offer_id", "scheduled_start", "scheduled_end", "status", "created_at") VALUES
(1, 2, 3, 1, 1, '2025-02-10 14:00:00+01', '2025-02-10 15:00:00+01', 'COMPLETED', '2025-02-01 10:00:00+01'),
(2, 2, 3, 2, 1, '2025-12-24 09:00:00+01', '2025-12-24 10:00:00+01', 'CONFIRMED', '2025-12-20 18:00:00+01'),
(3, 6, 5, 4, 2, '2025-03-20 10:00:00+01', '2025-03-20 10:45:00+01', 'COMPLETED', '2025-03-15 09:00:00+01'),
(4, 8, 7, 5, 3, '2025-07-05 18:00:00+01', '2025-07-05 19:00:00+01', 'CANCELLED', '2025-07-01 10:00:00+01'),
(5, 8, 5, 6, 2, '2026-01-15 12:00:00+01', '2026-01-15 13:00:00+01', 'PENDING', '2026-01-02 08:00:00+01'),
(6, 4, 7, 3, 3, '2025-12-31 20:00:00+01', '2025-12-31 21:00:00+01', 'COMPLETED', '2025-12-30 10:00:00+01'),
(7, 2, 5, 2, 2, '2026-01-03 10:00:00+01', '2026-01-03 11:00:00+01', 'CONFIRMED', '2026-01-01 12:00:00+01'),
(8, 6, 3, 4, 1, '2026-01-04 14:00:00+01', '2026-01-04 15:00:00+01', 'PENDING', '2026-01-02 18:00:00+01'),
(9, 8, 7, 6, 3, '2026-01-05 09:00:00+01', '2026-01-05 10:00:00+01', 'CANCELLED', '2026-01-01 09:00:00+01'),
(10, 4, 3, 3, 1, '2026-01-06 08:00:00+01', '2026-01-06 09:00:00+01', 'CONFIRMED', '2026-01-02 09:00:00+01'),
(11, 4, 3, 3, 1, '2026-01-07 08:00:00+01', '2026-01-07 09:00:00+01', 'CONFIRMED', '2026-01-02 09:00:00+01'),
(12, 4, 3, 3, 1, '2026-01-08 08:00:00+01', '2026-01-08 09:00:00+01', 'CONFIRMED', '2026-01-02 09:00:00+01'),
(13, 2, 3, 1, 1, '2025-11-15 16:00:00+01', '2025-11-15 17:00:00+01', 'COMPLETED', '2025-11-14 10:00:00+01'),
(14, 8, 5, 5, 2, '2026-01-02 21:00:00+01', '2026-01-02 22:00:00+01', 'PENDING', '2026-01-02 19:30:00+01'),
(15, 4, 7, 3, 3, '2026-02-14 18:00:00+01', '2026-02-14 20:00:00+01', 'CONFIRMED', '2026-01-02 10:00:00+01'),
(16, 2, 3, 2, 1, '2025-12-10 16:00:00+01', '2025-12-10 17:00:00+01', 'COMPLETED', '2025-12-08 10:00:00+01'),
(17, 8, 5, 5, 2, '2025-12-15 12:00:00+01', '2025-12-15 13:00:00+01', 'COMPLETED', '2025-12-14 09:00:00+01'),
(18, 6, 3, 4, 1, '2025-12-23 14:00:00+01', '2025-12-23 15:00:00+01', 'COMPLETED', '2025-12-20 11:00:00+01'),
(19, 4, 7, 3, 3, '2025-12-28 08:00:00+01', '2025-12-28 10:00:00+01', 'COMPLETED', '2025-12-27 18:00:00+01'),
(20, 2, 5, 1, 2, '2025-12-05 10:00:00+01', '2025-12-05 11:00:00+01', 'COMPLETED', '2025-12-01 10:00:00+01');

-- 8. DOSTĘPNOŚĆ (AVAILABILITY_SLOTS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."availability_slots" ("offer_id", "reservation_id", "start_time", "end_time", "latitude", "longitude", "created_at") VALUES
(1, 1, '2025-02-10 14:00:00+01', '2025-02-10 14:30:00+01', 52.2318, 21.0060, '2025-01-01 10:00:00+01'),
(1, 1, '2025-02-10 14:30:00+01', '2025-02-10 15:00:00+01', 52.2318, 21.0060, '2025-01-01 10:00:00+01'),
(1, 2, '2025-12-24 09:00:00+01', '2025-12-24 09:30:00+01', 52.2318, 21.0060, '2025-01-01 10:00:00+01'),
(1, 2, '2025-12-24 09:30:00+01', '2025-12-24 10:00:00+01', 52.2318, 21.0060, '2025-01-01 10:00:00+01'),
(1, NULL, '2026-01-02 18:00:00+01', '2026-01-02 18:30:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-02 18:30:00+01', '2026-01-02 19:00:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(2, 3, '2025-03-20 10:00:00+01', '2025-03-20 10:30:00+01', 52.2680, 20.9850, '2025-02-01 10:00:00+01'),
(2, 3, '2025-03-20 10:30:00+01', '2025-03-20 11:00:00+01', 52.2680, 20.9850, '2025-02-01 10:00:00+01'),
(2, 17, '2025-12-15 12:00:00+01', '2025-12-15 12:30:00+01', 52.2680, 20.9850, '2025-11-01 10:00:00+01'),
(2, 17, '2025-12-15 12:30:00+01', '2025-12-15 13:00:00+01', 52.2680, 20.9850, '2025-11-01 10:00:00+01'),
(2, NULL, '2026-01-03 08:00:00+01', '2026-01-03 08:30:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-03 08:30:00+01', '2026-01-03 09:00:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(3, 19, '2025-12-28 08:00:00+01', '2025-12-28 08:30:00+01', 52.2490, 21.0400, '2025-12-01 10:00:00+01'),
(3, 19, '2025-12-28 08:30:00+01', '2025-12-28 09:00:00+01', 52.2490, 21.0400, '2025-12-01 10:00:00+01'),
(3, 19, '2025-12-28 09:00:00+01', '2025-12-28 09:30:00+01', 52.2490, 21.0400, '2025-12-01 10:00:00+01'),
(3, 19, '2025-12-28 09:30:00+01', '2025-12-28 10:00:00+01', 52.2490, 21.0400, '2025-12-01 10:00:00+01'),
(3, 6, '2025-12-31 20:00:00+01', '2025-12-31 20:30:00+01', 52.2490, 21.0400, '2025-12-01 10:00:00+01'),
(3, 6, '2025-12-31 20:30:00+01', '2025-12-31 21:00:00+01', 52.2490, 21.0400, '2025-12-01 10:00:00+01'),
(1, NULL, '2026-01-05 08:00:00+01', '2026-01-05 08:30:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-05 08:30:00+01', '2026-01-05 09:00:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-05 09:00:00+01', '2026-01-05 09:30:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-05 09:30:00+01', '2026-01-05 10:00:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-05 10:00:00+01', '2026-01-05 10:30:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-05 10:30:00+01', '2026-01-05 11:00:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-06 08:00:00+01', '2026-01-06 08:30:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-06 08:30:00+01', '2026-01-06 09:00:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-07 08:00:00+01', '2026-01-07 08:30:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(1, NULL, '2026-01-07 08:30:00+01', '2026-01-07 09:00:00+01', 52.2318, 21.0060, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-05 15:00:00+01', '2026-01-05 15:30:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-05 15:30:00+01', '2026-01-05 16:00:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-05 16:00:00+01', '2026-01-05 16:30:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-05 16:30:00+01', '2026-01-05 17:00:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-06 15:00:00+01', '2026-01-06 15:30:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-06 15:30:00+01', '2026-01-06 16:00:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-07 15:00:00+01', '2026-01-07 15:30:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(2, NULL, '2026-01-07 15:30:00+01', '2026-01-07 16:00:00+01', 52.2680, 20.9850, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 10:00:00+01', '2026-01-10 10:30:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 10:30:00+01', '2026-01-10 11:00:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 11:00:00+01', '2026-01-10 11:30:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 11:30:00+01', '2026-01-10 12:00:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 12:00:00+01', '2026-01-10 12:30:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 12:30:00+01', '2026-01-10 13:00:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 13:00:00+01', '2026-01-10 13:30:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-10 13:30:00+01', '2026-01-10 14:00:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-11 10:00:00+01', '2026-01-11 10:30:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01'),
(3, NULL, '2026-01-11 10:30:00+01', '2026-01-11 11:00:00+01', 52.2490, 21.0400, '2026-01-01 10:00:00+01');

-- 9. PŁATNOŚCI I TRANSAKCJE (PAYMENTS & TRANSACTIONS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."payments" ("payment_id", "reservation_id", "payer_id", "payee_id", "amount_cents", "currency", "status", "created_at", "provider_ref") VALUES
(1, 1, 2, 3, 4500, 'PLN', 'PAID', '2025-02-01 10:05:00+01', 'STRIPE_TX_12345'),
(2, 2, 2, 3, 4500, 'PLN', 'PENDING', '2025-12-20 18:05:00+01', NULL),
(3, 3, 6, 5, 3500, 'PLN', 'PAID', '2025-03-15 09:05:00+01', 'STRIPE_TX_67890'),
(4, 4, 8, 7, 5000, 'PLN', 'REFUNDED', '2025-07-01 10:05:00+01', 'STRIPE_TX_REFUND_999'),
(5, 6, 4, 7, 5000, 'PLN', 'PAID', '2025-12-30 10:05:00+01', 'STRIPE_TX_SYLWESTER'),
(6, 7, 2, 5, 3500, 'PLN', 'PAID', '2026-01-01 12:05:00+01', 'STRIPE_TX_NEWYEAR'),
(7, 9, 8, 7, 5000, 'PLN', 'REFUNDED', '2026-01-01 09:05:00+01', 'STRIPE_TX_CANCELLED'),
(8, 10, 4, 3, 4500, 'PLN', 'PAID', '2026-01-02 09:05:00+01', 'STRIPE_TX_PACK_1'),
(9, 11, 4, 3, 4500, 'PLN', 'PAID', '2026-01-02 09:05:00+01', 'STRIPE_TX_PACK_2'),
(10, 12, 4, 3, 4500, 'PLN', 'PAID', '2026-01-02 09:05:00+01', 'STRIPE_TX_PACK_3'),
(11, 13, 2, 3, 4500, 'PLN', 'PAID', '2025-11-14 10:05:00+01', 'STRIPE_TX_OLD_1'),
(12, 15, 4, 7, 5000, 'PLN', 'PENDING', '2026-01-02 10:05:00+01', NULL),
(13, 16, 2, 3, 4500, 'PLN', 'PAID', '2025-12-10 17:00:00+01', 'STRIPE_TX_DEC_1'),
(14, 17, 8, 5, 3500, 'PLN', 'PAID', '2025-12-15 13:00:00+01', 'STRIPE_TX_DEC_2'),
(15, 18, 6, 3, 4500, 'PLN', 'PAID', '2025-12-23 15:00:00+01', 'STRIPE_TX_DEC_3'),
(16, 19, 4, 7, 10000, 'PLN', 'PAID', '2025-12-28 10:00:00+01', 'STRIPE_TX_DEC_4'),
(17, 20, 2, 5, 3500, 'PLN', 'PAID', '2025-12-05 11:00:00+01', 'STRIPE_TX_DEC_5');

INSERT INTO public."transactions" ("transaction_id", "user_id", "wallet_id", "reservation_id", "amount_cents", "balance_after_cents", "type", "description", "created_at") VALUES
(1, 2, 2, 1, -4500, 45500, 'PAYMENT', 'Opłata za spacer #1', '2025-02-01 10:05:00+01'),
(2, 3, 3, 1, 4500, 19500, 'PAYOUT', 'Wpływ za spacer #1', '2025-02-01 10:05:00+01'),
(3, 6, 6, 3, -3500, 2000, 'PAYMENT', 'Opłata za spacer #3 (Fafik)', '2025-03-15 09:05:00+01'),
(4, 5, 5, 3, 3500, 8500, 'PAYOUT', 'Wpływ za spacer #3', '2025-03-15 09:05:00+01'),
(5, 8, 8, 4, -5000, 25000, 'PAYMENT', 'Opłata za spacer #4', '2025-07-01 10:05:00+01'),
(6, 8, 8, 4, 5000, 30000, 'REFUND', 'Zwrot za anulowany spacer #4', '2025-07-04 12:00:00+01'),
(7, 4, 4, 6, -5000, 5000, 'PAYMENT', 'Spacer sylwestrowy', '2025-12-30 10:05:00+01'),
(8, 7, 7, 6, 5000, 5000, 'PAYOUT', 'Wpływ za spacer sylwestrowy', '2025-12-30 10:05:00+01'),
(9, 2, 2, 7, -3500, 42000, 'PAYMENT', 'Rezerwacja Luna', '2026-01-01 12:05:00+01'),
(10, 4, 4, 10, -4500, 500, 'PAYMENT', 'Spacer cykliczny 1/3', '2026-01-02 09:05:00+01'),
(11, 4, 4, 11, -4500, -4000, 'PAYMENT', 'Spacer cykliczny 2/3', '2026-01-02 09:05:00+01'),
(12, 4, 4, 12, -4500, -8500, 'PAYMENT', 'Spacer cykliczny 3/3', '2026-01-02 09:05:00+01'),
(13, 4, 4, NULL, 20000, 11500, 'TOPUP', 'Doładowanie konta BLIK', '2026-01-02 09:00:00+01'),
(14, 2, 2, 16, -4500, 37500, 'PAYMENT', 'Spacer Grudzień Luna', '2025-12-10 17:00:00+01'),
(15, 8, 8, 17, -3500, 26500, 'PAYMENT', 'Spacer Grudzień Ares', '2025-12-15 13:00:00+01'),
(16, 6, 6, 18, -4500, -2500, 'PAYMENT', 'Spacer Grudzień Fafik', '2025-12-23 15:00:00+01'),
(17, 4, 4, 19, -10000, 1500, 'PAYMENT', 'Spacer Grudzień Rex', '2025-12-28 10:00:00+01'),
(18, 2, 2, 20, -3500, 34000, 'PAYMENT', 'Spacer Grudzień Burek', '2025-12-05 11:00:00+01'),
(19, 3, 3, 16, 4500, 24000, 'PAYOUT', 'Zarobek Luna', '2025-12-10 17:00:00+01'),
(20, 5, 5, 17, 3500, 12000, 'PAYOUT', 'Zarobek Ares', '2025-12-15 13:00:00+01'),
(21, 3, 3, 18, 4500, 28500, 'PAYOUT', 'Zarobek Fafik', '2025-12-23 15:00:00+01'),
(22, 7, 7, 19, 10000, 15000, 'PAYOUT', 'Zarobek Rex (2h)', '2025-12-28 10:00:00+01'),
(23, 5, 5, 20, 3500, 15500, 'PAYOUT', 'Zarobek Burek', '2025-12-05 11:00:00+01');

-- 10. CZATY I WIADOMOŚCI (CHATS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."chats" ("chat_id", "reservation_id", "created_at") VALUES
(1, 1, '2025-02-01 10:10:00+01'), (2, 3, '2025-03-15 09:10:00+01'),
(3, 5, '2026-01-02 08:05:00+01'), (4, 8, '2026-01-02 18:05:00+01'),
(5, 14, '2026-01-02 19:35:00+01');

INSERT INTO public."chat_messages" ("message_id", "chat_id", "sender_id", "content", "sent_at") VALUES
(1, 1, 2, 'Cześć Piotr, czy możesz wziąć ulubioną zabawkę Burka?', '2025-02-01 10:11:00+01'),
(2, 1, 3, 'Jasne, nie ma problemu! Będę o 14:00.', '2025-02-01 10:15:00+01'),
(3, 2, 6, 'Dzień dobry Pani Kasiu, Fafik trochę szczeka na inne psy.', '2025-03-15 09:15:00+01'),
(4, 2, 5, 'Dzień dobry! Poradzę sobie, mam doświadczenie z terierami :)', '2025-03-15 09:30:00+01'),
(5, 3, 8, 'Czy w czwartek o 12:00 jest jeszcze wolne?', '2026-01-02 08:06:00+01'),
(6, 4, 6, 'Cześć, Fafik jest mały, ale zadziorny. Dasz radę?', '2026-01-02 18:06:00+01'),
(7, 5, 8, 'Ratuj! Muszę wyjść, czy podejdziesz jeszcze dziś?', '2026-01-02 19:36:00+01');

-- 11. SESJE GPS I PUNKTY (GPS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."gps_sessions" ("session_id", "reservation_id", "started_at", "stopped_at") VALUES
(1, 1, '2025-02-10 14:05:00+01', '2025-02-10 14:55:00+01'),
(2, 3, '2025-03-20 10:05:00+01', '2025-03-20 10:40:00+01'),
(3, 16, '2025-12-10 16:05:00+01', '2025-12-10 16:55:00+01'),
(4, 17, '2025-12-15 12:02:00+01', '2025-12-15 12:58:00+01'),
(5, 18, '2025-12-23 14:05:00+01', '2025-12-23 14:50:00+01'),
(6, 19, '2025-12-28 08:05:00+01', '2025-12-28 09:55:00+01'),
(7, 20, '2025-12-05 10:15:00+01', '2025-12-05 10:55:00+01');

INSERT INTO public."gps_points" ("point_id", "session_id", "latitude", "longitude", "recorded_at") VALUES
(1, 1, 52.2297, 21.0122, '2025-02-10 14:05:00+01'), (2, 1, 52.2300, 21.0130, '2025-02-10 14:15:00+01'),
(3, 1, 52.2310, 21.0140, '2025-02-10 14:30:00+01'), (4, 1, 52.2297, 21.0122, '2025-02-10 14:55:00+01'),
(5, 2, 52.2680, 20.9850, '2025-03-20 10:05:00+01'), (6, 2, 52.2700, 20.9900, '2025-03-20 10:20:00+01'),
(7, 2, 52.2680, 20.9850, '2025-03-20 10:40:00+01'), (8, 3, 52.2318, 21.0060, '2025-12-10 16:05:00+01'),
(9, 3, 52.2325, 21.0080, '2025-12-10 16:15:00+01'), (10, 3, 52.2350, 21.0100, '2025-12-10 16:30:00+01'),
(11, 3, 52.2330, 21.0070, '2025-12-10 16:45:00+01'), (12, 3, 52.2318, 21.0060, '2025-12-10 16:55:00+01'),
(13, 4, 52.2390, 20.9300, '2025-12-15 12:02:00+01'), (14, 4, 52.2410, 20.9350, '2025-12-15 12:15:00+01'),
(15, 4, 52.2430, 20.9400, '2025-12-15 12:30:00+01'), (16, 4, 52.2410, 20.9350, '2025-12-15 12:45:00+01'),
(17, 4, 52.2390, 20.9300, '2025-12-15 12:58:00+01'), (18, 5, 52.1400, 21.0500, '2025-12-23 14:05:00+01'),
(19, 5, 52.1350, 21.0550, '2025-12-23 14:20:00+01'), (20, 5, 52.1300, 21.0600, '2025-12-23 14:35:00+01'),
(21, 5, 52.1400, 21.0500, '2025-12-23 14:50:00+01'), (22, 6, 52.2490, 21.0400, '2025-12-28 08:05:00+01'),
(23, 6, 52.2450, 21.0500, '2025-12-28 08:25:00+01'), (24, 6, 52.2420, 21.0550, '2025-12-28 08:50:00+01'),
(25, 6, 52.2400, 21.0580, '2025-12-28 09:15:00+01'), (26, 6, 52.2450, 21.0500, '2025-12-28 09:35:00+01'),
(27, 6, 52.2490, 21.0400, '2025-12-28 09:55:00+01'), (28, 7, 52.2297, 21.0122, '2025-12-05 10:15:00+01'),
(29, 7, 52.2250, 21.0150, '2025-12-05 10:30:00+01'), (30, 7, 52.2297, 21.0122, '2025-12-05 10:55:00+01');

-- 12. OPINIE (REVIEWS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."reviews" ("review_id", "reservation_id", "author_id", "subject_user_id", "dog_id", "rating", "comment", "review_type", "created_at") VALUES
(1, 1, 2, 3, NULL, 5, 'Piotr to świetny opiekun, Burek wrócił zmęczony i szczęśliwy!', 'WALKER', '2025-02-10 16:00:00+01'),
(2, 1, 3, NULL, 1, 4, 'Burek jest grzeczny, ale trochę ciągnie na smyczy.', 'DOG', '2025-02-10 16:30:00+01'),
(3, 3, 6, 5, NULL, 5, 'Pani Kasia ma świetne podejście do małych szczekaczy.', 'WALKER', '2025-03-20 12:00:00+01'),
(4, 6, 4, 7, NULL, 5, 'Tomek uratował nam Sylwestra, Rex wybiegany!', 'WALKER', '2026-01-01 12:00:00+01'),
(5, 13, 2, 3, NULL, 5, 'Jak zawsze super.', 'WALKER', '2025-11-15 18:00:00+01'),
(6, 16, 2, 3, NULL, 5, 'Luna uwielbia spacery z Piotrem. Zawsze punktualnie.', 'WALKER', '2025-12-10 17:05:00+01'),
(7, 17, 8, 5, NULL, 5, 'Bałam się, że Kasia nie utrzyma Huskyego, ale dała radę! Polecam.', 'WALKER', '2025-12-15 13:10:00+01'),
(8, 18, 6, 3, NULL, 4, 'Wszystko super, ale spacer trwał 55 minut zamiast 60.', 'WALKER', '2025-12-23 15:15:00+01'),
(9, 19, 4, 7, NULL, 5, 'Tomek ma świetną kondycję, Rex wrócił padnięty. O to chodziło!', 'WALKER', '2025-12-28 10:05:00+01'),
(10, 19, 7, NULL, 3, 5, 'Rex to mądry pies, ale trzeba uważać na koty.', 'DOG', '2025-12-28 10:10:00+01'),
(11, 20, 2, 5, NULL, 3, 'Spacer ok, ale Kasia spóźniła się 15 minut i nie dała znać.', 'WALKER', '2025-12-05 11:30:00+01'),
(12, 16, 3, 2, 2, 5, 'Trasa w Parku Świętokrzyskim bardzo przyjemna, mało ludzi o tej porze.', 'WALK', '2025-12-10 17:10:00+01'),
(13, 17, 5, 8, 5, 5, 'Świetna pogoda do biegania. Zrobiliśmy dodatkowe kółko wokół stawu.', 'WALK', '2025-12-15 13:05:00+01'),
(14, 19, 7, 4, 3, 4, 'Spacer udany, ale główna alejka w parku jest w remoncie, musieliśmy obejść błoto.', 'WALK', '2025-12-28 10:15:00+01');

-- 13. ZDARZENIA DOMENOWE (DOMAIN_EVENTS)
-- ---------------------------------------------------------------------------------
INSERT INTO public."domain_events" ("event_id", "type", "actor_id", "target_dog_id", "target_reservation_id", "occurred_at", "description", "metadata_json") VALUES
(1, 'USER_CREATED', 2, NULL, NULL, '2025-01-02 11:30:00+01', 'Rejestracja użytkownika Anna', '{"ip": "192.168.1.15"}'),
(2, 'DOG_CREATED', 2, 1, NULL, '2025-01-02 12:30:00+01', 'Dodano psa Burek', NULL),
(3, 'RESERVATION_COMPLETED', 3, 1, 1, '2025-02-10 15:00:00+01', 'Spacer zakończony pomyślnie', '{"distance_km": 3.5}'),
(4, 'USER_CREATED', 6, NULL, NULL, '2025-03-10 14:20:00+01', 'Rejestracja użytkownika Michał', '{"ip": "192.168.1.55"}'),
(5, 'RESERVATION_CANCELLED', 8, NULL, 4, '2025-07-04 11:59:00+01', 'Anulowanie rezerwacji przez właściciela', '{"reason": "choroba"}');

-- 14. RESETOWANIE SEKWENCJI (SEQUENCES)
-- ---------------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('public.users', 'user_id'), coalesce(max(user_id), 1)) FROM public.users;
SELECT setval(pg_get_serial_sequence('public.wallets', 'wallet_id'), coalesce(max(wallet_id), 1)) FROM public.wallets;
SELECT setval(pg_get_serial_sequence('public.addresses', 'address_id'), coalesce(max(address_id), 1)) FROM public.addresses;
SELECT setval(pg_get_serial_sequence('public.dogs', 'dog_id'), coalesce(max(dog_id), 1)) FROM public.dogs;
SELECT setval(pg_get_serial_sequence('public.dog_photos', 'photo_id'), coalesce(max(photo_id), 1)) FROM public.dog_photos;
SELECT setval(pg_get_serial_sequence('public.offers', 'offer_id'), coalesce(max(offer_id), 1)) FROM public.offers;
SELECT setval(pg_get_serial_sequence('public.reservations', 'reservation_id'), coalesce(max(reservation_id), 1)) FROM public.reservations;
SELECT setval(pg_get_serial_sequence('public.availability_slots', 'slot_id'), coalesce(max(slot_id), 1)) FROM public.availability_slots;
SELECT setval(pg_get_serial_sequence('public.payments', 'payment_id'), coalesce(max(payment_id), 1)) FROM public.payments;
SELECT setval(pg_get_serial_sequence('public.transactions', 'transaction_id'), coalesce(max(transaction_id), 1)) FROM public.transactions;
SELECT setval(pg_get_serial_sequence('public.chats', 'chat_id'), coalesce(max(chat_id), 1)) FROM public.chats;
SELECT setval(pg_get_serial_sequence('public.chat_messages', 'message_id'), coalesce(max(message_id), 1)) FROM public.chat_messages;
SELECT setval(pg_get_serial_sequence('public.gps_sessions', 'session_id'), coalesce(max(session_id), 1)) FROM public.gps_sessions;
SELECT setval(pg_get_serial_sequence('public.gps_points', 'point_id'), coalesce(max(point_id), 1)) FROM public.gps_points;
SELECT setval(pg_get_serial_sequence('public.reviews', 'review_id'), coalesce(max(review_id), 1)) FROM public.reviews;
SELECT setval(pg_get_serial_sequence('public.domain_events', 'event_id'), coalesce(max(event_id), 1)) FROM public.domain_events;