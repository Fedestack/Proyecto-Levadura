package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.ListaDePrecios;
import com.fedestack.Levadura.model.Precio;
import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.repository.ListaDePreciosRepository;
import com.fedestack.Levadura.repository.PrecioRepository;
import com.fedestack.Levadura.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataLoaderService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PrecioRepository precioRepository;

    @Autowired    private ListaDePreciosRepository listaDePreciosRepository;

    @Transactional
    public void loadProductDataFromCsv() {
        String line;
        String csvFile = "C:\\Users\\fedes\\Desktop\\Proyecto Levadura\\levadura codigo\\Proyecto-Levadura\\archivos csv\\productos.csv";
        int lineNum = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String firstLine = br.readLine(); // Read the first header line
            if (firstLine == null) {
                throw new IOException("CSV file is empty.");
            }
            String[] headers = firstLine.split(";");

            // Map column index to price list name dynamically
            Map<Integer, String> priceListColumnMap = new HashMap<>();
            // The price list names start from the 4th column (index 3)
            // and go up to the 17th column (index 16)
            String[] hardcodedPriceListNames = {"Minorista", "Mayorista", "AMODIL", "EL TANO", "VIVANCO", "GLORIAS", "POMPEYA", "BRAZZI", "JUANCHOS", "GUERE GUERE/ABUELO", "TUTTO SAN FER", "RINCON / LINIERS", "JOAQUIN CHINO", "VILLA TERRA"};
            for (int i = 0; i < hardcodedPriceListNames.length; i++) {
                // Find the index of the hardcoded name in the actual headers
                for (int j = 0; j < headers.length; j++) {
                    if (headers[j].trim().equalsIgnoreCase(hardcodedPriceListNames[i])) {
                        priceListColumnMap.put(j, hardcodedPriceListNames[i]);
                        break;
                    }
                }
            }

            br.readLine(); // Skip the second header line (e.g., ";;;Lista 0;Listas 1;...")
            br.readLine(); // Skip the third header line (e.g., ";;;4;5;6;...")
            br.readLine(); // Skip the fourth line (e.g., ";;PANES;;;;...")

            // Ensure all expected ListaDePrecios exist in the database
            for (String name : hardcodedPriceListNames) {
                listaDePreciosRepository.findByNombre(name).orElseGet(() -> {
                    ListaDePrecios lp = new ListaDePrecios();
                    lp.setNombre(name);
                    return listaDePreciosRepository.save(lp);
                });
            }

            // Read data rows
            while ((line = br.readLine()) != null) {
                lineNum++;
                // Skip empty lines or lines that are not product data (e.g., category headers)
                if (line.trim().isEmpty() || line.startsWith(";;") || line.contains("Costeo Mercaderia")) {
                    continue;
                }

                String[] data = line.split(";");

                // Basic validation for product data row
                if (data.length < 20) { // Adjust based on the actual number of columns you expect for product data + prices
                    System.err.println("Skipping malformed line " + lineNum + ": " + line);
                    continue;
                }

                try {
                    String codigo = data[0].trim();
                    String rubro = data[1].trim();
                    String nombreProducto = data[2].trim();
                    String unidad = ""; // Assuming 'unidad' is not explicitly in the CSV, or needs to be derived

                    // Check for duplicate product codes (due to repeated data in CSV)
                    Optional<Producto> existingProducto = productoRepository.findByCodigo(codigo);
                    Producto producto;
                    if (existingProducto.isPresent()) {
                        producto = existingProducto.get();
                        // Update existing product if necessary
                        producto.setRubro(rubro);
                        producto.setNombre(nombreProducto);
                        producto.setUnidad(unidad); // Update unit if it's derived
                        // Update costoTotal if it's in the CSV and needs to be updated
                        // For now, we'll assume costoTotal is handled later or not updated from this CSV
                    } else {
                        producto = new Producto();
                        producto.setCodigo(codigo);
                        producto.setRubro(rubro);
                        producto.setNombre(nombreProducto);
                        producto.setUnidad(unidad); // Set unit
                        // Set costoTotal if it's in the CSV
                    }

                    // Parse costoTotal from the CSV (column 20, 0-indexed)
                    // The CSV has multiple empty columns before costoTotal, so it's actually column 20 (0-indexed)
                    // The CSV also has repeated product data at the end, so we need to be careful
                    // Based on the CSV, Costo Total is in column 20 (0-indexed)
                    if (data.length > 20 && !data[20].trim().isEmpty()) {
                        try {
                            String costoTotalStr = data[20].trim().replace("$", "").replace(" ", "").replace(".", "").replace(",", ".");
                            producto.setCostoTotal(new BigDecimal(costoTotalStr));
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Could not parse costoTotal for product " + codigo + ": " + data[20]);
                            producto.setCostoTotal(BigDecimal.ZERO);
                        }
                    }

                    producto = productoRepository.save(producto);

                    // Process prices for each list
                    for (Map.Entry<Integer, String> entry : priceListColumnMap.entrySet()) {
                        int colIndex = entry.getKey();
                        String priceListName = entry.getValue();

                        if (colIndex < data.length && !data[colIndex].trim().isEmpty()) {
                            String priceStr = data[colIndex].trim().replace("$", "").replace(" ", "").replace(".", "").replace(",", ".").replace("-", "0");
                            try {
                                BigDecimal priceValue = new BigDecimal(priceStr);

                                ListaDePrecios listaDePrecios = listaDePreciosRepository.findByNombre(priceListName)
                                        .orElseThrow(() -> new RuntimeException("Lista de Precios no encontrada: " + priceListName));

                                Optional<Precio> existingPrecio = precioRepository.findByProductoAndListaDePrecios(producto, listaDePrecios);
                                if (existingPrecio.isPresent()) {
                                    Precio precio = existingPrecio.get();
                                    precio.setValor(priceValue);
                                    precioRepository.save(precio);
                                } else {
                                    Precio precio = new Precio();
                                    precio.setProducto(producto);
                                    precio.setListaDePrecios(listaDePrecios);
                                    precio.setValor(priceValue);
                                    precioRepository.save(precio);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Warning: Could not parse price for product " + codigo + " in list " + priceListName + ": " + data[colIndex]);
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error processing line " + lineNum + ": " + line + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
