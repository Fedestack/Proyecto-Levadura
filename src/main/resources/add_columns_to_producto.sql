ALTER TABLE producto ADD COLUMN rubro VARCHAR(255);
ALTER TABLE producto ADD COLUMN costeo_mercaderia_sin_mo DECIMAL(10, 2);
ALTER TABLE producto ADD COLUMN gastos_generales_sacado_del_informe_mensual DECIMAL(10, 2);
ALTER TABLE producto ADD COLUMN costo_total DECIMAL(10, 2);