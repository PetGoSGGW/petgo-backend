-- =================================================================================
-- SEEDER DANYCH - APLIKACJA DOG WALKING
-- =================================================================================

-- 1. SŁOWNIKI (BREEDS)
-- Dodajemy kilka popularnych ras poza podstawowymi
INSERT INTO public."breeds" ("breed_code", "name") VALUES 
('LAB', 'Labrador Retriever'),
('GSD', 'Owczarek Niemiecki'),
('BEA', 'Beagle'),
('BUL', 'Buldog Francuski'),
('MIX', 'Mieszaniec'),
('HUS', 'Husky Syberyjski'),
('YOR', 'Yorkshire Terrier'),
('COR', 'Welsh Corgi Pembroke'),
('BOX', 'Bokser'),
('GLD', 'Golden Retriever');

-- 2. UŻYTKOWNICY (USERS)
-- Hasło: 'password' ($2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO)
INSERT INTO public."users" ("user_id", "email", "first_name", "last_name", "password_hash", "username", "role", "is_active", "created_at") VALUES 
(1, 'admin@homelab.pl', 'Admin', 'Sysop', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'admin', 'ADMIN', TRUE, '2025-01-01 10:00:00+01'),
-- Właściciele psów
(2, 'anna.nowak@email.pl', 'Anna', 'Nowak', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'annan', 'USER', TRUE, '2025-01-02 11:30:00+01'),
(4, 'jan.wisniewski@email.pl', 'Jan', 'Wiśniewski', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'janw', 'USER', TRUE, '2025-01-04 09:15:00+01'),
(6, 'michal.lewandowski@email.pl', 'Michał', 'Lewandowski', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'michall', 'USER', TRUE, '2025-03-10 14:20:00+01'),
(8, 'zofia.mazur@email.pl', 'Zofia', 'Mazur', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'zofiam', 'USER', TRUE, '2025-05-20 18:00:00+01'),
-- Opiekunowie (Walkers)
(3, 'piotr.kowalski@email.pl', 'Piotr', 'Kowalski', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'piotrk', 'USER', TRUE, '2025-01-03 15:45:00+01'),
(5, 'kasia.wojcik@email.pl', 'Katarzyna', 'Wójcik', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'kasiaw', 'USER', TRUE, '2025-02-15 09:00:00+01'),
(7, 'tomek.kaczmarek@email.pl', 'Tomasz', 'Kaczmarek', '$2a$10$lqqjAOU0abE3F.sJtlX5oOlVf.5LkmuweOsJw7O1Oy1DNGMPc3qLO', 'tomekk', 'USER', TRUE, '2025-06-01 12:00:00+01');

-- 3. PORTFELE (WALLETS)
INSERT INTO public."wallets" ("wallet_id", "user_id", "balance_cents", "currency", "created_at", "version") VALUES 
(1, 1, 0, 'PLN', '2025-01-01 10:00:00+01', 1),
(2, 2, 45500, 'PLN', '2025-01-02 11:30:00+01', 2), -- Anna (po transakcjach)
(3, 3, 19500, 'PLN', '2025-01-03 15:45:00+01', 2), -- Piotr (Walker)
(4, 4, 10000, 'PLN', '2025-01-04 09:15:00+01', 1), -- Jan (100 PLN na start)
(5, 5, 8500, 'PLN', '2025-02-15 09:00:00+01', 1), -- Kasia (Walker)
(6, 6, 2000, 'PLN', '2025-03-10 14:20:00+01', 1), -- Michał
(7, 7, 0, 'PLN', '2025-06-01 12:00:00+01', 1), -- Tomek (Nowy Walker)
(8, 8, 30000, 'PLN', '2025-05-20 18:00:00+01', 1); -- Zofia

-- 4. ADRESY (ADDRESSES) - Różne dzielnice Warszawy
INSERT INTO public."addresses" ("address_id", "user_id", "city", "street", "home_number", "zipcode", "label", "latitude", "longitude", "created_at") VALUES 
(1, 2, 'Warszawa', 'Marszałkowska', '10', '00-001', 'Dom', 52.2297, 21.0122, '2025-01-02 12:00:00+01'), -- Śródmieście
(2, 3, 'Warszawa', 'Złota', '44', '00-020', 'Biuro', 52.2318, 21.0060, '2025-01-03 16:00:00+01'), -- Śródmieście
(3, 5, 'Warszawa', 'Mickiewicza', '15', '01-517', 'Dom', 52.2680, 20.9850, '2025-02-15 09:10:00+01'), -- Żoliborz (Kasia)
(4, 6, 'Warszawa', 'KEN', '20', '02-797', 'Mieszkanie', 52.1400, 21.0500, '2025-03-10 14:30:00+01'), -- Ursynów (Michał)
(5, 7, 'Warszawa', 'Targowa', '12', '03-731', 'Dom', 52.2490, 21.0400, '2025-06-01 12:10:00+01'), -- Praga (Tomek)
(6, 8, 'Warszawa', 'Górczewska', '124', '01-460', 'Dom', 52.2390, 20.9300, '2025-05-20 18:10:00+01'); -- Wola (Zofia)

-- 5. PSY (DOGS)
INSERT INTO public."dogs" ("dog_id", "owner_id", "name", "breed_code", "size", "weight_kg", "notes", "is_active", "created_at") VALUES 
(1, 2, 'Burek', 'MIX', 'MEDIUM', 15.5, 'Lubi gonić rowery, boi się burzy.', TRUE, '2025-01-02 12:30:00+01'),
(2, 2, 'Luna', 'LAB', 'LARGE', 28.0, 'Bardzo przyjazna, alergia na drób.', TRUE, '2025-01-02 12:35:00+01'),
(3, 4, 'Rex', 'GSD', 'LARGE', 35.0, 'Wymaga silnej ręki.', TRUE, '2025-01-04 10:00:00+01'),
(4, 6, 'Fafik', 'YOR', 'SMALL', 3.5, 'Szczeka na duże psy.', TRUE, '2025-03-11 09:00:00+01'),
(5, 8, 'Ares', 'HUS', 'LARGE', 26.0, 'Ma mnóstwo energii, potrzebuje biegania.', TRUE, '2025-05-21 10:00:00+01'),
(6, 8, 'Daisy', 'GLD', 'LARGE', 30.0, 'Kocha wszystkich ludzi.', TRUE, '2025-05-21 10:05:00+01');

-- 6. ZDJĘCIA PSÓW (DOG_PHOTOS)
INSERT INTO public."dog_photos" ("photo_id", "dog_id", "url", "uploaded_at") VALUES 
(1, 1, 'https://homelab-storage.local/dogs/burek.jpg', '2025-01-05 10:00:00+01'),
(2, 2, 'https://homelab-storage.local/dogs/luna.jpg', '2025-01-05 10:05:00+01'),
(3, 4, 'https://homelab-storage.local/dogs/fafik.jpg', '2025-03-11 09:05:00+01'),
(4, 5, 'https://homelab-storage.local/dogs/ares.jpg', '2025-05-21 10:10:00+01');

-- 7. OFERTY (OFFERS)
INSERT INTO public."offers" ("offer_id", "walker_id", "description", "price_cents", "is_active", "created_at") VALUES 
(1, 3, 'Długie spacery po parkach na Mokotowie i w Centrum', 4500, TRUE, '2025-01-03 17:00:00+01'),
(2, 5, 'Spacery Żoliborz / Bielany - małe psy mile widziane', 3500, TRUE, '2025-02-15 10:00:00+01'),
(3, 7, 'Praga Północ i Południe - spacery z bieganiem', 5000, TRUE, '2025-06-01 13:00:00+01');

-- 8. REZERWACJE (RESERVATIONS)
-- #1: Zakończona (Anna -> Piotr)
INSERT INTO public."reservations" ("reservation_id", "owner_id", "walker_id", "dog_id", "offer_id", "scheduled_start", "scheduled_end", "status", "created_at") VALUES 
(1, 2, 3, 1, 1, '2025-02-10 14:00:00+01', '2025-02-10 15:00:00+01', 'COMPLETED', '2025-02-01 10:00:00+01'),
-- #2: Przyszła (Anna -> Piotr, grudzień 2025)
(2, 2, 3, 2, 1, '2025-12-24 09:00:00+01', '2025-12-24 10:00:00+01', 'CONFIRMED', '2025-12-20 18:00:00+01'),
-- #3: Zakończona (Michał -> Kasia, Marzec 2025)
(3, 6, 5, 4, 2, '2025-03-20 10:00:00+01', '2025-03-20 10:45:00+01', 'COMPLETED', '2025-03-15 09:00:00+01'),
-- #4: Anulowana (Zofia -> Tomek, Lipiec 2025)
(4, 8, 7, 5, 3, '2025-07-05 18:00:00+01', '2025-07-05 19:00:00+01', 'CANCELLED', '2025-07-01 10:00:00+01'),
-- #5: Oczekująca (Zofia -> Kasia, Styczeń 2026 - przyszłość dla systemu)
(5, 8, 5, 6, 2, '2026-01-15 12:00:00+01', '2026-01-15 13:00:00+01', 'PENDING', '2026-01-02 08:00:00+01');

-- 9. PŁATNOŚCI (PAYMENTS)
INSERT INTO public."payments" ("payment_id", "reservation_id", "payer_id", "payee_id", "amount_cents", "currency", "status", "created_at", "provider_ref") VALUES 
(1, 1, 2, 3, 4500, 'PLN', 'PAID', '2025-02-01 10:05:00+01', 'STRIPE_TX_12345'),
(2, 2, 2, 3, 4500, 'PLN', 'PENDING', '2025-12-20 18:05:00+01', NULL),
(3, 3, 6, 5, 3500, 'PLN', 'PAID', '2025-03-15 09:05:00+01', 'STRIPE_TX_67890'),
(4, 4, 8, 7, 5000, 'PLN', 'REFUNDED', '2025-07-01 10:05:00+01', 'STRIPE_TX_REFUND_999');

-- 10. TRANSAKCJE (TRANSACTIONS)
INSERT INTO public."transactions" ("transaction_id", "user_id", "wallet_id", "reservation_id", "amount_cents", "balance_after_cents", "type", "description", "created_at") VALUES 
-- Rezerwacja #1
(1, 2, 2, 1, -4500, 45500, 'PAYMENT', 'Opłata za spacer #1', '2025-02-01 10:05:00+01'),
(2, 3, 3, 1, 4500, 19500, 'PAYOUT', 'Wpływ za spacer #1', '2025-02-01 10:05:00+01'),
-- Rezerwacja #3
(3, 6, 6, 3, -3500, 2000, 'PAYMENT', 'Opłata za spacer #3 (Fafik)', '2025-03-15 09:05:00+01'),
(4, 5, 5, 3, 3500, 8500, 'PAYOUT', 'Wpływ za spacer #3', '2025-03-15 09:05:00+01'),
-- Rezerwacja #4 (Anulowana - Płatność i Zwrot)
(5, 8, 8, 4, -5000, 25000, 'PAYMENT', 'Opłata za spacer #4', '2025-07-01 10:05:00+01'),
(6, 8, 8, 4, 5000, 30000, 'REFUND', 'Zwrot za anulowany spacer #4', '2025-07-04 12:00:00+01');

-- 11. CZATY I WIADOMOŚCI (CHATS)
INSERT INTO public."chats" ("chat_id", "reservation_id", "created_at") VALUES 
(1, 1, '2025-02-01 10:10:00+01'),
(2, 3, '2025-03-15 09:10:00+01'),
(3, 5, '2026-01-02 08:05:00+01');

INSERT INTO public."chat_messages" ("message_id", "chat_id", "sender_id", "content", "sent_at") VALUES 
-- Czat #1
(1, 1, 2, 'Cześć Piotr, czy możesz wziąć ulubioną zabawkę Burka?', '2025-02-01 10:11:00+01'),
(2, 1, 3, 'Jasne, nie ma problemu! Będę o 14:00.', '2025-02-01 10:15:00+01'),
-- Czat #2
(3, 2, 6, 'Dzień dobry Pani Kasiu, Fafik trochę szczeka na inne psy.', '2025-03-15 09:15:00+01'),
(4, 2, 5, 'Dzień dobry! Poradzę sobie, mam doświadczenie z terierami :)', '2025-03-15 09:30:00+01'),
-- Czat #3 (Najnowszy)
(5, 3, 8, 'Czy w czwartek o 12:00 jest jeszcze wolne?', '2026-01-02 08:06:00+01');

-- 12. SESJE GPS (GPS_SESSIONS)
INSERT INTO public."gps_sessions" ("session_id", "reservation_id", "started_at", "stopped_at") VALUES 
(1, 1, '2025-02-10 14:05:00+01', '2025-02-10 14:55:00+01'),
(2, 3, '2025-03-20 10:05:00+01', '2025-03-20 10:40:00+01');

INSERT INTO public."gps_points" ("point_id", "session_id", "latitude", "longitude", "recorded_at") VALUES 
-- Sesja #1 (Centrum)
(1, 1, 52.2297, 21.0122, '2025-02-10 14:05:00+01'),
(2, 1, 52.2300, 21.0130, '2025-02-10 14:15:00+01'),
(3, 1, 52.2310, 21.0140, '2025-02-10 14:30:00+01'),
(4, 1, 52.2297, 21.0122, '2025-02-10 14:55:00+01'),
-- Sesja #2 (Żoliborz)
(5, 2, 52.2680, 20.9850, '2025-03-20 10:05:00+01'),
(6, 2, 52.2700, 20.9900, '2025-03-20 10:20:00+01'),
(7, 2, 52.2680, 20.9850, '2025-03-20 10:40:00+01');

-- 13. OPINIE (REVIEWS)
INSERT INTO public."reviews" ("review_id", "reservation_id", "author_id", "subject_user_id", "dog_id", "rating", "comment", "review_type", "created_at") VALUES 
(1, 1, 2, 3, NULL, 5, 'Piotr to świetny opiekun, Burek wrócił zmęczony i szczęśliwy!', 'WALKER', '2025-02-10 16:00:00+01'),
(2, 1, 3, NULL, 1, 4, 'Burek jest grzeczny, ale trochę ciągnie na smyczy.', 'DOG', '2025-02-10 16:30:00+01'),
(3, 3, 6, 5, NULL, 5, 'Pani Kasia ma świetne podejście do małych szczekaczy.', 'WALKER', '2025-03-20 12:00:00+01');

-- 14. ZDARZENIA DOMENOWE (DOMAIN_EVENTS)
INSERT INTO public."domain_events" ("event_id", "type", "actor_id", "target_dog_id", "target_reservation_id", "occurred_at", "description", "metadata_json") VALUES 
(1, 'USER_CREATED', 2, NULL, NULL, '2025-01-02 11:30:00+01', 'Rejestracja użytkownika Anna', '{"ip": "192.168.1.15"}'),
(2, 'DOG_CREATED', 2, 1, NULL, '2025-01-02 12:30:00+01', 'Dodano psa Burek', NULL),
(3, 'RESERVATION_COMPLETED', 3, 1, 1, '2025-02-10 15:00:00+01', 'Spacer zakończony pomyślnie', '{"distance_km": 3.5}'),
(4, 'USER_CREATED', 6, NULL, NULL, '2025-03-10 14:20:00+01', 'Rejestracja użytkownika Michał', '{"ip": "192.168.1.55"}'),
(5, 'RESERVATION_CANCELLED', 8, NULL, 4, '2025-07-04 11:59:00+01', 'Anulowanie rezerwacji przez właściciela', '{"reason": "choroba"}');


-- =================================================================================
-- ROZSZERZONY SEEDER - NOWE REZERWACJE I POWIĄZANIA
-- Data symulacji: 2026-01-02 (Piątek)
-- =================================================================================

-- 1. NOWE REZERWACJE (RESERVATIONS)
-- ID 6-15
INSERT INTO public."reservations" ("reservation_id", "owner_id", "walker_id", "dog_id", "offer_id", "scheduled_start", "scheduled_end", "status", "created_at") VALUES 
-- ZAKOŃCZONA (Sylwester): Jan (4) -> Tomek (7) z psem Rex (3)
(6, 4, 7, 3, 3, '2025-12-31 20:00:00+01', '2025-12-31 21:00:00+01', 'COMPLETED', '2025-12-30 10:00:00+01'),

-- POTWIERDZONA (Na jutro): Anna (2) -> Kasia (5) z psem Luna (2)
(7, 2, 5, 2, 2, '2026-01-03 10:00:00+01', '2026-01-03 11:00:00+01', 'CONFIRMED', '2026-01-01 12:00:00+01'),

-- OCZEKUJĄCA (Na pojutrze): Michał (6) -> Piotr (3) z psem Fafik (4)
(8, 6, 3, 4, 1, '2026-01-04 14:00:00+01', '2026-01-04 15:00:00+01', 'PENDING', '2026-01-02 18:00:00+01'),

-- ANULOWANA (Przez Walkera): Zofia (8) -> Tomek (7) z psem Daisy (6)
(9, 8, 7, 6, 3, '2026-01-05 09:00:00+01', '2026-01-05 10:00:00+01', 'CANCELLED', '2026-01-01 09:00:00+01'),

-- CYKLICZNE SPACERY (Jan -> Piotr z Rexem) - Pakiet na przyszły tydzień
(10, 4, 3, 3, 1, '2026-01-06 08:00:00+01', '2026-01-06 09:00:00+01', 'CONFIRMED', '2026-01-02 09:00:00+01'),
(11, 4, 3, 3, 1, '2026-01-07 08:00:00+01', '2026-01-07 09:00:00+01', 'CONFIRMED', '2026-01-02 09:00:00+01'),
(12, 4, 3, 3, 1, '2026-01-08 08:00:00+01', '2026-01-08 09:00:00+01', 'CONFIRMED', '2026-01-02 09:00:00+01'),

-- ZAKOŃCZONA (Dawna): Anna (2) -> Piotr (3) z psem Burek (1)
(13, 2, 3, 1, 1, '2025-11-15 16:00:00+01', '2025-11-15 17:00:00+01', 'COMPLETED', '2025-11-14 10:00:00+01'),

-- OCZEKUJĄCA (Dziś wieczór - last minute): Zofia (8) -> Kasia (5) z psem Ares (5)
(14, 8, 5, 5, 2, '2026-01-02 21:00:00+01', '2026-01-02 22:00:00+01', 'PENDING', '2026-01-02 19:30:00+01'),

-- POTWIERDZONA (Daleka przyszłość): Jan (4) -> Tomek (7)
(15, 4, 7, 3, 3, '2026-02-14 18:00:00+01', '2026-02-14 20:00:00+01', 'CONFIRMED', '2026-01-02 10:00:00+01');

-- 2. PŁATNOŚCI (PAYMENTS)
-- Generujemy płatności dla rezerwacji potwierdzonych, zakończonych i anulowanych (zwroty)
INSERT INTO public."payments" ("payment_id", "reservation_id", "payer_id", "payee_id", "amount_cents", "currency", "status", "created_at", "provider_ref") VALUES 
-- Res 6 (Zakończona - Sylwester - Drożej)
(5, 6, 4, 7, 5000, 'PLN', 'PAID', '2025-12-30 10:05:00+01', 'STRIPE_TX_SYLWESTER'),
-- Res 7 (Potwierdzona - Anna)
(6, 7, 2, 5, 3500, 'PLN', 'PAID', '2026-01-01 12:05:00+01', 'STRIPE_TX_NEWYEAR'),
-- Res 9 (Anulowana - Zofia - Zwrot)
(7, 9, 8, 7, 5000, 'PLN', 'REFUNDED', '2026-01-01 09:05:00+01', 'STRIPE_TX_CANCELLED'),
-- Res 10, 11, 12 (Pakiet Jana - 3 spacery po 45 PLN)
(8, 10, 4, 3, 4500, 'PLN', 'PAID', '2026-01-02 09:05:00+01', 'STRIPE_TX_PACK_1'),
(9, 11, 4, 3, 4500, 'PLN', 'PAID', '2026-01-02 09:05:00+01', 'STRIPE_TX_PACK_2'),
(10, 12, 4, 3, 4500, 'PLN', 'PAID', '2026-01-02 09:05:00+01', 'STRIPE_TX_PACK_3'),
-- Res 13 (Stara zakończona)
(11, 13, 2, 3, 4500, 'PLN', 'PAID', '2025-11-14 10:05:00+01', 'STRIPE_TX_OLD_1'),
-- Res 15 (Przyszła walentynkowa)
(12, 15, 4, 7, 5000, 'PLN', 'PENDING', '2026-01-02 10:05:00+01', NULL);

-- 3. TRANSAKCJE (TRANSACTIONS)
-- Odzwierciedlenie przepływu pieniędzy w portfelach
INSERT INTO public."transactions" ("transaction_id", "user_id", "wallet_id", "reservation_id", "amount_cents", "balance_after_cents", "type", "description", "created_at") VALUES 
-- Res 6 (Jan płaci Tomkowi)
(7, 4, 4, 6, -5000, 5000, 'PAYMENT', 'Spacer sylwestrowy', '2025-12-30 10:05:00+01'),
(8, 7, 7, 6, 5000, 5000, 'PAYOUT', 'Wpływ za spacer sylwestrowy', '2025-12-30 10:05:00+01'),

-- Res 7 (Anna płaci Kasi)
(9, 2, 2, 7, -3500, 42000, 'PAYMENT', 'Rezerwacja Luna', '2026-01-01 12:05:00+01'),
-- Kasi nie dodajemy payoutu, bo status to CONFIRMED (pieniądze zamrożone do czasu wykonania usługi)

-- Res 10, 11, 12 (Jan płaci Piotrowi - pakiet)
(10, 4, 4, 10, -4500, 500, 'PAYMENT', 'Spacer cykliczny 1/3', '2026-01-02 09:05:00+01'),
(11, 4, 4, 11, -4500, -4000, 'PAYMENT', 'Spacer cykliczny 2/3', '2026-01-02 09:05:00+01'), -- Jan wszedł na debet (doładowanie potrzebne!)
(12, 4, 4, 12, -4500, -8500, 'PAYMENT', 'Spacer cykliczny 3/3', '2026-01-02 09:05:00+01');

-- DOŁADOWANIE PORTFELA JANA (żeby nie miał debetu)
INSERT INTO public."transactions" ("transaction_id", "user_id", "wallet_id", "reservation_id", "amount_cents", "balance_after_cents", "type", "description", "created_at") VALUES 
(13, 4, 4, NULL, 20000, 11500, 'TOPUP', 'Doładowanie konta BLIK', '2026-01-02 09:00:00+01');

-- 4. AKTUALIZACJA STANÓW PORTFELI (WALLETS)
-- Musimy zaktualizować balance_cents na podstawie powyższych transakcji
-- Anna (2): -3500 (Res 7) -> 42000
UPDATE public."wallets" SET "balance_cents" = 42000, "version" = "version" + 1 WHERE "user_id" = 2;

-- Jan (4): -5000 (Res 6) - 13500 (Res 10-12) + 20000 (Topup) = Start 10000 - 18500 + 20000 = 11500
UPDATE public."wallets" SET "balance_cents" = 11500, "version" = "version" + 1 WHERE "user_id" = 4;

-- Tomek (7): +5000 (Res 6) -> 5000
UPDATE public."wallets" SET "balance_cents" = 5000, "version" = "version" + 1 WHERE "user_id" = 7;

-- 5. NOWE CZATY (CHATS & MESSAGES)
-- Do rezerwacji PENDING (negocjacje)
INSERT INTO public."chats" ("chat_id", "reservation_id", "created_at") VALUES 
(4, 8, '2026-01-02 18:05:00+01'), -- Michał -> Piotr
(5, 14, '2026-01-02 19:35:00+01'); -- Zofia -> Kasia

INSERT INTO public."chat_messages" ("message_id", "chat_id", "sender_id", "content", "sent_at") VALUES 
-- Michał pyta Piotra
(6, 4, 6, 'Cześć, Fafik jest mały, ale zadziorny. Dasz radę?', '2026-01-02 18:06:00+01'),
-- Zofia pyta Kasię (Last Minute)
(7, 5, 8, 'Ratuj! Muszę wyjść, czy podejdziesz jeszcze dziś?', '2026-01-02 19:36:00+01');

-- 6. NOWE OPINIE (REVIEWS)
-- Tylko do rezerwacji zakończonych (COMPLETED)
INSERT INTO public."reviews" ("review_id", "reservation_id", "author_id", "subject_user_id", "dog_id", "rating", "comment", "review_type", "created_at") VALUES 
-- Opinia do spaceru sylwestrowego (Res 6)
(4, 6, 4, 7, NULL, 5, 'Tomek uratował nam Sylwestra, Rex wybiegany!', 'WALKER', '2026-01-01 12:00:00+01'),
-- Opinia do starego spaceru (Res 13)
(5, 13, 2, 3, NULL, 5, 'Jak zawsze super.', 'WALKER', '2025-11-15 18:00:00+01');

-- Weryfikacja
-- SELECT * FROM RESERVATIONS ORDER BY CREATED_AT DESC;

-- =================================================================================
-- SEEDER: HISTORIA I OPINIE (REVIEWS & HISTORY)
-- Data symulacji: 2026-01-02
-- Cel: Zapełnienie profili walkerów opiniami i historią
-- =================================================================================

-- 1. ZAKOŃCZONE REZERWACJE (Grudzień 2025)
INSERT INTO public."reservations" ("reservation_id", "owner_id", "walker_id", "dog_id", "offer_id", "scheduled_start", "scheduled_end", "status", "created_at") VALUES 
-- Anna (2) -> Piotr (3): Standardowy spacer z Luną
(16, 2, 3, 2, 1, '2025-12-10 16:00:00+01', '2025-12-10 17:00:00+01', 'COMPLETED', '2025-12-08 10:00:00+01'),

-- Zofia (8) -> Kasia (5): Pierwszy spacer z Aresem (Husky)
(17, 8, 5, 5, 2, '2025-12-15 12:00:00+01', '2025-12-15 13:00:00+01', 'COMPLETED', '2025-12-14 09:00:00+01'),

-- Michał (6) -> Piotr (3): Fafik (York) przed świętami
(18, 6, 3, 4, 1, '2025-12-23 14:00:00+01', '2025-12-23 15:00:00+01', 'COMPLETED', '2025-12-20 11:00:00+01'),

-- Jan (4) -> Tomek (7): Długi spacer z Rexem (Owczarek)
(19, 4, 7, 3, 3, '2025-12-28 08:00:00+01', '2025-12-28 10:00:00+01', 'COMPLETED', '2025-12-27 18:00:00+01'),

-- Anna (2) -> Kasia (5): Spacer próbny (ocena 3/5 - gorszy scenariusz)
(20, 2, 5, 1, 2, '2025-12-05 10:00:00+01', '2025-12-05 11:00:00+01', 'COMPLETED', '2025-12-01 10:00:00+01');

-- 2. OPINIE (REVIEWS)
-- Generujemy opinie dla powyższych rezerwacji
INSERT INTO public."reviews" ("review_id", "reservation_id", "author_id", "subject_user_id", "dog_id", "rating", "comment", "review_type", "created_at") VALUES 
-- Opinia do Res 16 (Anna o Piotrze) - 5 gwiazdek
(6, 16, 2, 3, NULL, 5, 'Luna uwielbia spacery z Piotrem. Zawsze punktualnie.', 'WALKER', '2025-12-10 17:05:00+01'),

-- Opinia do Res 17 (Zofia o Kasi) - 5 gwiazdek
(7, 17, 8, 5, NULL, 5, 'Bałam się, że Kasia nie utrzyma Huskyego, ale dała radę! Polecam.', 'WALKER', '2025-12-15 13:10:00+01'),

-- Opinia do Res 18 (Michał o Piotrze) - 4 gwiazdki (Drobna uwaga)
(8, 18, 6, 3, NULL, 4, 'Wszystko super, ale spacer trwał 55 minut zamiast 60.', 'WALKER', '2025-12-23 15:15:00+01'),

-- Opinia do Res 19 (Jan o Tomku) - 5 gwiazdek
(9, 19, 4, 7, NULL, 5, 'Tomek ma świetną kondycję, Rex wrócił padnięty. O to chodziło!', 'WALKER', '2025-12-28 10:05:00+01'),
-- Opinia zwrotna (Tomek o psie Rex) - Walker ocenia psa
(10, 19, 7, NULL, 3, 5, 'Rex to mądry pies, ale trzeba uważać na koty.', 'DOG', '2025-12-28 10:10:00+01'),

-- Opinia do Res 20 (Anna o Kasi) - 3 gwiazdki (Mieszana opinia)
(11, 20, 2, 5, NULL, 3, 'Spacer ok, ale Kasia spóźniła się 15 minut i nie dała znać.', 'WALKER', '2025-12-05 11:30:00+01');

-- 3. UZUPEŁNIENIE PŁATNOŚCI (PAYMENTS)
-- Aby zachować spójność danych (zakończone rezerwacje muszą być opłacone)
INSERT INTO public."payments" ("payment_id", "reservation_id", "payer_id", "payee_id", "amount_cents", "currency", "status", "created_at", "provider_ref") VALUES 
(13, 16, 2, 3, 4500, 'PLN', 'PAID', '2025-12-10 17:00:00+01', 'STRIPE_TX_DEC_1'),
(14, 17, 8, 5, 3500, 'PLN', 'PAID', '2025-12-15 13:00:00+01', 'STRIPE_TX_DEC_2'),
(15, 18, 6, 3, 4500, 'PLN', 'PAID', '2025-12-23 15:00:00+01', 'STRIPE_TX_DEC_3'),
(16, 19, 4, 7, 10000, 'PLN', 'PAID', '2025-12-28 10:00:00+01', 'STRIPE_TX_DEC_4'), -- 2h spaceru
(17, 20, 2, 5, 3500, 'PLN', 'PAID', '2025-12-05 11:00:00+01', 'STRIPE_TX_DEC_5');

-- 4. LOG TRANSAKCJI (TRANSACTIONS)
INSERT INTO public."transactions" ("transaction_id", "user_id", "wallet_id", "reservation_id", "amount_cents", "balance_after_cents", "type", "description", "created_at") VALUES 
-- Płatności wychodzące od właścicieli
(14, 2, 2, 16, -4500, 37500, 'PAYMENT', 'Spacer Grudzień Luna', '2025-12-10 17:00:00+01'),
(15, 8, 8, 17, -3500, 26500, 'PAYMENT', 'Spacer Grudzień Ares', '2025-12-15 13:00:00+01'),
(16, 6, 6, 18, -4500, -2500, 'PAYMENT', 'Spacer Grudzień Fafik', '2025-12-23 15:00:00+01'), -- Michał był na minusie zanim doładował w marcu
(17, 4, 4, 19, -10000, 1500, 'PAYMENT', 'Spacer Grudzień Rex', '2025-12-28 10:00:00+01'),
(18, 2, 2, 20, -3500, 34000, 'PAYMENT', 'Spacer Grudzień Burek', '2025-12-05 11:00:00+01'),

-- Wpływy do Walkerów (Payouts)
(19, 3, 3, 16, 4500, 24000, 'PAYOUT', 'Zarobek Luna', '2025-12-10 17:00:00+01'),
(20, 5, 5, 17, 3500, 12000, 'PAYOUT', 'Zarobek Ares', '2025-12-15 13:00:00+01'),
(21, 3, 3, 18, 4500, 28500, 'PAYOUT', 'Zarobek Fafik', '2025-12-23 15:00:00+01'),
(22, 7, 7, 19, 10000, 15000, 'PAYOUT', 'Zarobek Rex (2h)', '2025-12-28 10:00:00+01'),
(23, 5, 5, 20, 3500, 15500, 'PAYOUT', 'Zarobek Burek', '2025-12-05 11:00:00+01');

-- 5. AKTUALIZACJA PORTFELI (WALLETS)
-- Dodajemy zarobki do portfeli Walkerów
UPDATE public."wallets" SET "balance_cents" = "balance_cents" + 9000 WHERE "user_id" = 3; -- Piotr (+2 spacery)
UPDATE public."wallets" SET "balance_cents" = "balance_cents" + 7000 WHERE "user_id" = 5; -- Kasia (+2 spacery)
UPDATE public."wallets" SET "balance_cents" = "balance_cents" + 10000 WHERE "user_id" = 7; -- Tomek (+1 długi spacer)

-- Odejmujemy od właścicieli (zakładamy, że mieli środki lub system pozwolił na debet w historii)
UPDATE public."wallets" SET "balance_cents" = "balance_cents" - 8000 WHERE "user_id" = 2; -- Anna
UPDATE public."wallets" SET "balance_cents" = "balance_cents" - 3500 WHERE "user_id" = 8; -- Zofia


-- =================================================================================
-- SEEDER: DANE GPS (SESSIONS & POINTS)
-- Dla rezerwacji zakończonych: 16, 17, 18, 19, 20
-- =================================================================================

-- 1. SESJE GPS (GPS_SESSIONS)
-- Czas trwania dopasowany do rezerwacji (z lekkim marginesem na "uruchomienie" aplikacji)
INSERT INTO public."gps_sessions" ("session_id", "reservation_id", "started_at", "stopped_at") VALUES 
-- Res 16 (Piotr, Centrum): 1h
(3, 16, '2025-12-10 16:05:00+01', '2025-12-10 16:55:00+01'),

-- Res 17 (Kasia, Wola): 1h
(4, 17, '2025-12-15 12:02:00+01', '2025-12-15 12:58:00+01'),

-- Res 18 (Piotr, Ursynów): 55 min (zgodnie z opinią o krótszym spacerze)
(5, 18, '2025-12-23 14:05:00+01', '2025-12-23 14:50:00+01'),

-- Res 19 (Tomek, Praga): 2h (Długi spacer)
(6, 19, '2025-12-28 08:05:00+01', '2025-12-28 09:55:00+01'),

-- Res 20 (Kasia, Śródmieście): Krótki spacer próbny
(7, 20, '2025-12-05 10:15:00+01', '2025-12-05 10:55:00+01');

-- 2. PUNKTY GPS (GPS_POINTS)
-- Symulacja tras w odpowiednich dzielnicach Warszawy
INSERT INTO public."gps_points" ("point_id", "session_id", "latitude", "longitude", "recorded_at") VALUES 
-- === TRASA 1: Res 16 (Centrum, koło Pałacu Kultury) ===
(8, 3, 52.2318, 21.0060, '2025-12-10 16:05:00+01'), -- Start (Biuro)
(9, 3, 52.2325, 21.0080, '2025-12-10 16:15:00+01'),
(10, 3, 52.2350, 21.0100, '2025-12-10 16:30:00+01'), -- Park Świętokrzyski
(11, 3, 52.2330, 21.0070, '2025-12-10 16:45:00+01'),
(12, 3, 52.2318, 21.0060, '2025-12-10 16:55:00+01'), -- Powrót

-- === TRASA 2: Res 17 (Wola, Park Moczydło) ===
(13, 4, 52.2390, 20.9300, '2025-12-15 12:02:00+01'), -- Start (Górczewska)
(14, 4, 52.2410, 20.9350, '2025-12-15 12:15:00+01'), -- Wejście do parku
(15, 4, 52.2430, 20.9400, '2025-12-15 12:30:00+01'), -- Głębiej w parku
(16, 4, 52.2410, 20.9350, '2025-12-15 12:45:00+01'),
(17, 4, 52.2390, 20.9300, '2025-12-15 12:58:00+01'), -- Powrót

-- === TRASA 3: Res 18 (Ursynów, Las Kabacki - kawałek) ===
(18, 5, 52.1400, 21.0500, '2025-12-23 14:05:00+01'), -- Start (KEN)
(19, 5, 52.1350, 21.0550, '2025-12-23 14:20:00+01'), -- W stronę lasu
(20, 5, 52.1300, 21.0600, '2025-12-23 14:35:00+01'), -- Skraj lasu
(21, 5, 52.1400, 21.0500, '2025-12-23 14:50:00+01'), -- Szybki powrót

-- === TRASA 4: Res 19 (Praga, Park Skaryszewski - długa trasa) ===
(22, 6, 52.2490, 21.0400, '2025-12-28 08:05:00+01'), -- Start (Targowa)
(23, 6, 52.2450, 21.0500, '2025-12-28 08:25:00+01'), -- Spacer alejkami
(24, 6, 52.2420, 21.0550, '2025-12-28 08:50:00+01'), -- Nad jeziorkiem
(25, 6, 52.2400, 21.0580, '2025-12-28 09:15:00+01'), -- Druga strona parku
(26, 6, 52.2450, 21.0500, '2025-12-28 09:35:00+01'), -- Powrót
(27, 6, 52.2490, 21.0400, '2025-12-28 09:55:00+01'), -- Koniec

-- === TRASA 5: Res 20 (Śródmieście Południowe) ===
(28, 7, 52.2297, 21.0122, '2025-12-05 10:15:00+01'), -- Start (Marszałkowska)
(29, 7, 52.2250, 21.0150, '2025-12-05 10:30:00+01'), -- Plac Zbawiciela
(30, 7, 52.2297, 21.0122, '2025-12-05 10:55:00+01'); -- Powrót


-- =================================================================================
-- SEEDER: OPINIE O SPACERZE (TYPE: WALK)
-- Walker ocenia/opisuje przebieg trasy i warunki
-- =================================================================================

INSERT INTO public."reviews" ("review_id", "reservation_id", "author_id", "subject_user_id", "dog_id", "rating", "comment", "review_type", "created_at") VALUES 
-- 1. Do Rezerwacji 16 (Piotr -> Anna o spacerze z Luną)
-- Spacer był w centrum, Piotr zgłasza warunki.
(12, 16, 3, 2, 2, 5, 'Trasa w Parku Świętokrzyskim bardzo przyjemna, mało ludzi o tej porze.', 'WALK', '2025-12-10 17:10:00+01'),

-- 2. Do Rezerwacji 17 (Kasia -> Zofia o spacerze z Aresem)
-- Spacer z Huskym, Kasia zgłasza, że trasa była odpowiednia dla aktywnego psa.
(13, 17, 5, 8, 5, 5, 'Świetna pogoda do biegania. Zrobiliśmy dodatkowe kółko wokół stawu.', 'WALK', '2025-12-15 13:05:00+01'),

-- 3. Do Rezerwacji 19 (Tomek -> Jan o spacerze z Rexem)
-- Długi spacer, Tomek zgłasza utrudnienia (remont).
(14, 19, 7, 4, 3, 4, 'Spacer udany, ale główna alejka w parku jest w remoncie, musieliśmy obejść błoto.', 'WALK', '2025-12-28 10:15:00+01');