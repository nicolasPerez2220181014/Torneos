-- V6__Insert_sample_data.sql
-- Datos simulados para el dashboard

-- Insertar usuarios (solo si no existen)
INSERT INTO users (id, email, full_name, role, created_at) VALUES
('770e8400-e29b-41d4-a716-446655440001', 'admin@esport.com', 'Admin Principal', 'ORGANIZER', NOW() - INTERVAL '30 days'),
('770e8400-e29b-41d4-a716-446655440002', 'organizer1@esport.com', 'Carlos Mendoza', 'ORGANIZER', NOW() - INTERVAL '25 days'),
('770e8400-e29b-41d4-a716-446655440003', 'organizer2@esport.com', 'Ana García', 'ORGANIZER', NOW() - INTERVAL '20 days'),
('770e8400-e29b-41d4-a716-446655440004', 'subadmin1@esport.com', 'Luis Rodríguez', 'SUBADMIN', NOW() - INTERVAL '15 days'),
('770e8400-e29b-41d4-a716-446655440005', 'subadmin2@esport.com', 'María López', 'SUBADMIN', NOW() - INTERVAL '10 days'),
('770e8400-e29b-41d4-a716-446655440006', 'user1@gmail.com', 'Pedro Sánchez', 'USER', NOW() - INTERVAL '8 days'),
('770e8400-e29b-41d4-a716-446655440007', 'user2@gmail.com', 'Laura Martín', 'USER', NOW() - INTERVAL '7 days'),
('770e8400-e29b-41d4-a716-446655440008', 'user3@gmail.com', 'Diego Torres', 'USER', NOW() - INTERVAL '6 days'),
('770e8400-e29b-41d4-a716-446655440009', 'user4@gmail.com', 'Sofia Ruiz', 'USER', NOW() - INTERVAL '5 days'),
('770e8400-e29b-41d4-a716-446655440010', 'user5@gmail.com', 'Andrés Morales', 'USER', NOW() - INTERVAL '4 days'),
('770e8400-e29b-41d4-a716-446655440011', 'user6@gmail.com', 'Carmen Jiménez', 'USER', NOW() - INTERVAL '3 days'),
('770e8400-e29b-41d4-a716-446655440012', 'user7@gmail.com', 'Roberto Vega', 'USER', NOW() - INTERVAL '2 days'),
('770e8400-e29b-41d4-a716-446655440013', 'user8@gmail.com', 'Elena Castro', 'USER', NOW() - INTERVAL '1 day'),
('770e8400-e29b-41d4-a716-446655440014', 'user9@gmail.com', 'Javier Herrera', 'USER', NOW()),
('770e8400-e29b-41d4-a716-446655440015', 'user10@gmail.com', 'Natalia Ramos', 'USER', NOW())
ON CONFLICT (email) DO NOTHING;

-- Insertar torneos usando IDs existentes de categorías y game_types
INSERT INTO tournaments (id, organizer_id, category_id, game_type_id, name, description, is_paid, max_free_capacity, start_date_time, end_date_time, status, created_at) VALUES
-- Torneos activos/en progreso
('880e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', '4e698a52-1539-4855-9333-35bc595c3df6', '325a62b6-9b53-44ad-b798-26192461a16a', 'Copa Mundial LoL 2024', 'Torneo profesional de League of Legends con premios en efectivo', true, 0, NOW() + INTERVAL '2 days', NOW() + INTERVAL '5 days', 'PUBLISHED', NOW() - INTERVAL '15 days'),
('880e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440002', 'c3b4481b-cc8f-4412-85da-b2fd76772a1e', '03b9ae39-2237-4c6c-94d0-2e13b70baf63', 'Torneo Amateur CS2', 'Competencia amateur de Counter-Strike 2', false, 64, NOW() + INTERVAL '1 day', NOW() + INTERVAL '3 days', 'PUBLISHED', NOW() - INTERVAL '10 days'),
('880e8400-e29b-41d4-a716-446655440003', '770e8400-e29b-41d4-a716-446655440003', '73b74893-35ca-4c61-b5a4-49a9f77d6d06', 'a4e48ac5-e77a-4795-b87a-37852930d4e8', 'Liga Juvenil Valorant', 'Torneo para jugadores menores de 18 años', false, 32, NOW() + INTERVAL '3 days', NOW() + INTERVAL '6 days', 'PUBLISHED', NOW() - INTERVAL '8 days'),

-- Torneos completados
('880e8400-e29b-41d4-a716-446655440004', '770e8400-e29b-41d4-a716-446655440001', '73b74893-35ca-4c61-b5a4-49a9f77d6d06', '924012d4-6d1a-4bc9-9746-faa25ccf7e52', 'Copa Universitaria FIFA', 'Torneo entre universidades', true, 0, NOW() - INTERVAL '10 days', NOW() - INTERVAL '7 days', 'FINISHED', NOW() - INTERVAL '20 days'),
('880e8400-e29b-41d4-a716-446655440005', '770e8400-e29b-41d4-a716-446655440002', 'cdd3b5ac-0bfb-4a16-8e55-edf2ab548922', '924012d4-6d1a-4bc9-9746-faa25ccf7e52', 'Battle Royale Fortnite', 'Torneo masivo de Fortnite', false, 100, NOW() - INTERVAL '15 days', NOW() - INTERVAL '12 days', 'FINISHED', NOW() - INTERVAL '25 days'),

-- Torneos en borrador
('880e8400-e29b-41d4-a716-446655440006', '770e8400-e29b-41d4-a716-446655440003', '4e698a52-1539-4855-9333-35bc595c3df6', '325a62b6-9b53-44ad-b798-26192461a16a', 'Próximo Torneo LoL Pro', 'En preparación para el próximo mes', true, 0, NOW() + INTERVAL '30 days', NOW() + INTERVAL '33 days', 'DRAFT', NOW() - INTERVAL '5 days'),
('880e8400-e29b-41d4-a716-446655440007', '770e8400-e29b-41d4-a716-446655440001', 'c3b4481b-cc8f-4412-85da-b2fd76772a1e', '03b9ae39-2237-4c6c-94d0-2e13b70baf63', 'CS2 Summer Cup', 'Torneo de verano en planificación', false, 48, NOW() + INTERVAL '25 days', NOW() + INTERVAL '28 days', 'DRAFT', NOW() - INTERVAL '3 days'),

-- Más torneos para estadísticas
('880e8400-e29b-41d4-a716-446655440008', '770e8400-e29b-41d4-a716-446655440002', 'c3b4481b-cc8f-4412-85da-b2fd76772a1e', 'a4e48ac5-e77a-4795-b87a-37852930d4e8', 'Valorant Champions', 'Campeonato nacional de Valorant', true, 0, NOW() + INTERVAL '7 days', NOW() + INTERVAL '10 days', 'PUBLISHED', NOW() - INTERVAL '12 days'),
('880e8400-e29b-41d4-a716-446655440009', '770e8400-e29b-41d4-a716-446655440003', '73b74893-35ca-4c61-b5a4-49a9f77d6d06', '64eb75ed-e537-445a-9d37-475ad1e22e48', 'Dota 2 Pro League', 'Liga profesional de Dota 2', true, 0, NOW() + INTERVAL '14 days', NOW() + INTERVAL '17 days', 'PUBLISHED', NOW() - INTERVAL '6 days'),
('880e8400-e29b-41d4-a716-446655440010', '770e8400-e29b-41d4-a716-446655440001', 'cdd3b5ac-0bfb-4a16-8e55-edf2ab548922', '924012d4-6d1a-4bc9-9746-faa25ccf7e52', 'Fortnite Weekend', 'Torneo de fin de semana', false, 80, NOW() + INTERVAL '21 days', NOW() + INTERVAL '23 days', 'PUBLISHED', NOW() - INTERVAL '4 days')
ON CONFLICT (id) DO NOTHING;