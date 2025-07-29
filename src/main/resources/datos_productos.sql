DELETE FROM precio;
DELETE FROM producto;
DELETE FROM lista_de_precios;

-- Insertar las listas de precios
INSERT INTO lista_de_precios (id, nombre) VALUES
(1, 'Minorista'),
(2, 'Mayorista'),
(3, 'AMODIL'),
(4, 'EL TANO'),
(5, 'VIVANCO'),
(6, 'GLORIAS'),
(7, 'POMPEYA'),
(8, 'BRAZZI'),
(9, 'JUANCHOS'),
(10, 'GUERE GUERE/ABUELO'),
(11, 'TUTTO SAN FER'),
(12, 'RINCON / LINIERS'),
(13, 'JOAQUIN CHINO'),
(14, 'VILLA TERRA');

-- Insertar los productos
INSERT INTO producto (codigo, nombre, rubro, costo_total) VALUES
('0001', 'PAN COMUN', 'PANES', 749.48),
('0002', 'FRANCES / FRANCES C/ SEMILLAS X 500GR', 'PANES', 749.48),
('0003', 'PAN PARA BUDIN', 'PANES', 0.00),
-- ... (se omiten el resto de los productos por brevedad)
('2510', 'LATA COCIDA', 'LATA COCIDA', 0.00);

-- Insertar los precios
-- Precios para PAN COMUN (producto con código 0001)
INSERT INTO precio (producto_id, lista_de_precios_id, valor) VALUES
((SELECT id FROM producto WHERE codigo = '0001'), 1, 3300.00),
((SELECT id FROM producto WHERE codigo = '0001'), 2, 1980.00),
((SELECT id FROM producto WHERE codigo = '0001'), 3, 1980.00),
((SELECT id FROM producto WHERE codigo = '0001'), 4, 1870.00),
((SELECT id FROM producto WHERE codigo = '0001'), 5, 1760.00),
((SELECT id FROM producto WHERE codigo = '0001'), 6, 1870.00),
((SELECT id FROM producto WHERE codigo = '0001'), 7, 2090.00),
((SELECT id FROM producto WHERE codigo = '0001'), 8, 1980.00),
((SELECT id FROM producto WHERE codigo = '0001'), 9, 1650.00),
((SELECT id FROM producto WHERE codigo = '0001'), 10, 2090.00),
((SELECT id FROM producto WHERE codigo = '0001'), 11, 2090.00),
((SELECT id FROM producto WHERE codigo = '0001'), 12, 1430.00),
((SELECT id FROM producto WHERE codigo = '0001'), 13, 1540.00),
((SELECT id FROM producto WHERE codigo = '0001'), 14, 1650.00);

-- Precios para FRANCES / FRANCES C/ SEMILLAS X 500GR (producto con código 0002)
INSERT INTO precio (producto_id, lista_de_precios_id, valor) VALUES
((SELECT id FROM producto WHERE codigo = '0002'), 1, 1900.00),
((SELECT id FROM producto WHERE codigo = '0002'), 2, 1045.00),
((SELECT id FROM producto WHERE codigo = '0002'), 14, 950.00);

-- ... (se omiten el resto de los precios por brevedad)
