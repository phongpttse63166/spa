INSERT INTO public.spa (id, create_by, create_time, image, name, status) VALUES (1, '1', '31-05-2021', null, 'Spa A', 0);
INSERT INTO public.spa (id, create_by, create_time, image, name, status) VALUES (2, '1', '31-05-2021', null, 'Spa B', 0);
INSERT INTO public.spa (id, create_by, create_time, image, name, status) VALUES (3, '1', '31-05-2021', null, 'Spa C', 0);
INSERT INTO public.spa (id, create_by, create_time, image, name, status) VALUES (4, '1', '31-05-2021', null, 'Spa D', 1);

INSERT INTO public.spa_service (id, create_time, create_by, description, name, price, status, spa_id) VALUES (1, '31-05-2021', '1', 'abc', 'Service A', 300000, 0, 1);
INSERT INTO public.spa_service (id, create_time, create_by, description, name, price, status, spa_id) VALUES (2, '31-05-2021', '1', 'bcd', 'Service B', 260000, 0, 1);
INSERT INTO public.spa_service (id, create_time, create_by, description, name, price, status, spa_id) VALUES (3, '31-05-2021', '1', 'cde', 'Service C', 130000, 0, 1);
INSERT INTO public.spa_service (id, create_time, create_by, description, name, price, status, spa_id) VALUES (4, '31-05-2021', '1', 'def', 'Service D', 80000, 0, 1);
INSERT INTO public.spa_service (id, create_time, create_by, description, name, price, status, spa_id) VALUES (5, '31-05-2021', '1', 'efg', 'Service E', 3400, 0, 1);
INSERT INTO public.spa_service (id, create_time, create_by, description, name, price, status, spa_id) VALUES (6, '31-05-2021', '1', 'fgh', 'Service F', 128000, 0, 1);

INSERT INTO public."user" (id, address, email, fullname, password, phone) VALUES (1, '14 Kỳ Đồng', 'phong.pham29@gmail.com', 'Phạm Trần Thanh Phong', '123', '090');
INSERT INTO public."user" (id, address, email, fullname, password, phone) VALUES (2, '23 Quang Trung', 'thanh.nguyen23@gmail.com', 'Nguyễn Kim Thanh', '123', '091');
INSERT INTO public."user" (id, address, email, fullname, password, phone) VALUES (3, '34 Lê Văn Sỹ', 'hien.vu48@gmail.com', 'Vũ Đức Hiển', '123', '092');
INSERT INTO public."user" (id, address, email, fullname, password, phone) VALUES (4, '58 Phạm Văn Chiêu', 'tri.nguyen58@gmail.com', 'Nguyễn Mịnh Trí', '123', '093');
INSERT INTO public."user" (id, address, email, fullname, password, phone) VALUES (5, '234 Pham Văn Chiêu', 'ngoc.nguyen17@gmail.com', 'Nguyễn Minh Ngọc', '123', '094');