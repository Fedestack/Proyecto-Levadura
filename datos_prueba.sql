INSERT INTO cliente (nombre, direccion, telefono, cuit, usuario_id) VALUES
('Panadería La Esquina', 'Av. Siempre Viva 742', '11-1234-5678', '30-12345678-9', NULL),
('Kiosco El Sol', 'Calle Falsa 123', '11-2345-6789', '30-23456789-0', NULL),
('Supermercado El Ahorro', 'Rivadavia 5000', '11-3456-7890', '30-34567890-1', NULL),
('Cafetería Central', 'Corrientes 348', '11-4567-8901', '30-45678901-2', NULL),
('Restaurante Don Julio', 'Gurruchaga 2102', '11-5678-9012', '30-56789012-3', NULL),
('Hotel Buenos Aires', 'Av. de Mayo 1370', '11-6789-0123', '30-67890123-4', NULL),
('Pizzería El Trébol', 'Av. del Libertador 4500', '11-7890-1234', '30-78901234-5', NULL),
('Bar de la Plaza', 'Defensa 1098', '11-8901-2345', '30-89012345-6', NULL),
('Almacén de Ramos Generales', 'Balcarce 450', '11-9012-3456', '30-90123456-7', NULL),
('Despensa Doña Rosa', 'Humberto 1º 340', '11-0123-4567', '30-01234567-8', NULL),
('Fiambrería El Buen Gusto', 'Carlos Calvo 960', '11-1122-3344', '30-11223344-9', NULL),
('Colegio Nacional', 'Bolívar 263', '11-2233-4455', '30-22334455-0', NULL),
('Hospital Central', 'Uspallata 3300', '11-3344-5566', '30-33445566-1', NULL),
('Club Atlético', 'Brandsen 805', '11-4455-6677', '30-44556677-2', NULL),
('Teatro Colón', 'Cerrito 628', '11-5566-7788', '30-55667788-3', NULL);

INSERT INTO producto (codigo, nombre, descripcion, precioUnitario, unidad) VALUES
-- Panes --
('PAN-FLA', 'Pan Flauta', 'Clásico pan francés, ideal para sandwiches.', 2.50, 'UNIDAD'),
('PAN-MIG', 'Pan de Miga', 'Pan de molde sin corteza, para sandwiches de miga.', 8.00, 'PAQUETE'),
('PAN-SAL', 'Pan de Salvado', 'Pan integral con salvado, rico en fibra.', 3.00, 'UNIDAD'),
('PAN-CEN', 'Pan de Centeno', 'Pan de sabor intenso y color oscuro.', 3.50, 'UNIDAD'),
('PAN-BAG', 'Baguette', 'Pan francés largo y delgado.', 2.75, 'UNIDAD'),
('PAN-CIAB', 'Ciabatta', 'Pan italiano de corteza crujiente y miga aireada.', 3.25, 'UNIDAD'),
('PAN-HAM', 'Pan de Hamburguesa', 'Panecillos suaves para hamburguesas.', 4.00, 'PAQUETE-4'),
('PAN-PAN', 'Pan de Pancho', 'Panecillos para hot dogs.', 3.50, 'PAQUETE-6'),
('PAN-ARA', 'Pan Árabe', 'Pan plano y hueco, ideal para rellenar.', 5.00, 'PAQUETE-5'),
('PAN-CHA', 'Chipá', 'Panecillo de queso tradicional.', 1.50, 'UNIDAD'),

-- Facturas --
('FAC-MED', 'Medialuna de Manteca', 'Clásica medialuna dulce.', 1.20, 'UNIDAD'),
('FAC-GRA', 'Medialuna de Grasa', 'Medialuna salada y hojaldrada.', 1.10, 'UNIDAD'),
('FAC-VIG', 'Vigilante', 'Factura alargada con membrillo.', 1.30, 'UNIDAD'),
('FAC-BOL', 'Bola de Fraile', 'Factura redonda rellena de dulce de leche.', 1.50, 'UNIDAD'),
('FAC-SAC', 'Sacramento', 'Factura con membrillo y azúcar.', 1.40, 'UNIDAD'),
('FAC-CHUR', 'Churro', 'Churro simple o relleno de dulce de leche.', 1.60, 'UNIDAD'),
('FAC-PAL', 'Palmerita', 'Hojaldre acaramelado en forma de corazón.', 1.00, 'UNIDAD'),
('FAC-CAN', 'Cañoncito', 'Cañoncito de hojaldre relleno de dulce de leche.', 1.70, 'UNIDAD'),
('FAC-CRE', 'Crema Pastelera', 'Factura con crema pastelera y azúcar quemada.', 1.50, 'UNIDAD'),
('FAC-MEM', 'Librito de Membrillo', 'Hojaldre relleno de membrillo.', 1.40, 'UNIDAD'),

-- Masas Finas --
('MAS-LEM', 'Lemon Pie', 'Tarta de limón con merengue.', 15.00, 'PORCION'),
('MAS-RIC', 'Torta de Ricota', 'Clásica torta de ricota y limón.', 12.00, 'PORCION'),
('MAS-COC', 'Torta de Coco y DDL', 'Torta con base de coco y dulce de leche.', 14.00, 'PORCION'),
('MAS-CHO', 'Torta de Chocolate', 'Torta húmeda de chocolate con fudge.', 18.00, 'PORCION'),
('MAS-FRUT', 'Tarta de Frutillas', 'Tarta con crema pastelera y frutillas frescas.', 16.00, 'PORCION'),
('MAS-PAS', 'Pastafrola', 'Tarta de membrillo clásica.', 10.00, 'PORCION'),
('MAS-BRO', 'Brownie', 'Brownie de chocolate con nueces.', 4.00, 'UNIDAD'),
('MAS-ALF', 'Alfajor de Maicena', 'Alfajor de maicena con dulce de leche.', 2.00, 'UNIDAD'),
('MAS-ROG', 'Torta Rogel', 'Capas de hojaldre con dulce de leche y merengue.', 17.00, 'PORCION'),
('MAS-SEL', 'Selva Negra', 'Torta de chocolate, crema y cerezas.', 20.00, 'PORCION'),

-- Sandwiches --
('SAN-J&Q', 'Sandwich de Jamón y Queso', 'Sandwich de miga de jamón y queso.', 5.00, 'UNIDAD'),
('SAN-CRUDO', 'Sandwich de Crudo y Queso', 'Sandwich de miga de jamón crudo y queso.', 6.50, 'UNIDAD'),
('SAN-POL', 'Sandwich de Pollo', 'Pebete de pollo, lechuga y tomate.', 7.00, 'UNIDAD'),
('SAN-MIL', 'Sandwich de Milanesa', 'Sandwich de milanesa completo.', 12.00, 'UNIDAD'),
('SAN-VEG', 'Sandwich Vegetariano', 'Pan integral con vegetales grillados y hummus.', 8.00, 'UNIDAD'),

-- Pizzas y Tartas Saladas --
('PIZ-MUZ', 'Pizza de Muzzarella', 'Pizza grande de muzzarella.', 25.00, 'UNIDAD'),
('PIZ-JAM', 'Pizza de Jamón y Morrones', 'Pizza grande con jamón y morrones.', 30.00, 'UNIDAD'),
('TAR-J&Q', 'Tarta de Jamón y Queso', 'Tarta individual de jamón y queso.', 9.00, 'UNIDAD'),
('TAR-VER', 'Tarta de Verdura', 'Tarta individual de espinaca y salsa blanca.', 8.50, 'UNIDAD'),
('TAR-CAL', 'Tarta de Calabaza', 'Tarta individual de calabaza y queso.', 8.50, 'UNIDAD'),

-- Especialidades --
('ESP-PAND', 'Pan Dulce', 'Pan dulce con frutas abrillantadas y frutos secos (por temporada).', 22.00, 'KG'),
('ESP-ROS', 'Rosca de Pascua', 'Rosca de reyes con crema pastelera (por temporada).', 20.00, 'UNIDAD'),
('ESP-PRE', 'Prepizza', 'Masa de pizza lista para hornear.', 10.00, 'UNIDAD'),
('ESP-PAS', 'Pascualina', 'Masa para tarta hojaldrada.', 6.00, 'PAQUETE'),
('ESP-EMP', 'Tapas de Empanada', 'Tapas de empanada para horno.', 4.50, 'DOCENA'),

-- Bebidas --
('BEB-AGU', 'Agua Mineral', 'Botella de agua sin gas 500ml.', 3.00, 'UNIDAD'),
('BEB-GAS', 'Gaseosa Línea Coca-Cola', 'Lata de gaseosa 354ml.', 4.00, 'UNIDAD'),
('BEB-CAF', 'Café para llevar', 'Vaso de café caliente.', 5.00, 'UNIDAD'),
('BEB-JUG', 'Jugo de Naranja', 'Botella de jugo de naranja exprimido 500ml.', 7.00, 'UNIDAD');
