package m03.bitcoinmanager;

/**
 *System.getPropery("user.home")
 * 
 * 
 * 
 * 
 * 
 * 
 * @author tonyj
 */


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BitcoinManager {
    
    private static List<String[]> cotizacionData = new ArrayList<>();

    /*--------------------------------------------------------------------*/
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            try {
                MostrarMenuPrincipal();
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        CrearDirectorio();
                        break;
                    case 2:
                         cargarDatosCotizaciones();
                        MostrarMenuAnalisis();
                        int opcionAnalisis = scanner.nextInt();
                        scanner.nextLine();
                        switch (opcionAnalisis) {
                            case 1:
                                RealizarPedidoValor();
                                break;
                            case 2:
                                RealizarPedidoValorAvg();
                                break;
                            default:
                                System.out.println("Opción no válida");
                        }
                        break;
                    case 3:
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida");
                }


            } catch (Exception e) {
                System.out.println("Error: entrada no válida");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void CrearDirectorio() {
        String currentUsersHomeDir = System.getProperty("user.home");
        File mainFolder = new File(currentUsersHomeDir + "/BitcoinsManager");
        File cotizacionesFolder = new File(mainFolder.getPath() + "/cotizaciones");

        if (!mainFolder.exists()) {
            mainFolder.mkdir();
            cotizacionesFolder.mkdir();
            new File(currentUsersHomeDir + "/BitcoinsManager/carteras").mkdir();
        }

        System.out.println("Bitcoin manager by Anthony Jacome v1.0");

        File cotizacionFile = new File(cotizacionesFolder.getPath() + "/BTC-USD.csv");
        if (!cotizacionFile.exists()) {
            System.out.println("No se encontró el archivo BTC-USD.csv en la carpeta 'cotizaciones'.");
            return;
        }
        System.out.println("Archivo BTC-USD.csv cargado.");
        cargarDatosCotizaciones(cotizacionFile); 
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        String pedido = scanner.nextLine();
    }

    /*--------------------------------------------------------------------*/
    private static void MostrarMenuPrincipal() {
        System.out.println("------Menu Principal----------");
        System.out.println("1. Crear Directorio ");
        System.out.println("2. Análisis de cotizaciones  ");
        System.out.println("3. Salir");
         System.out.println("------------------------------");
    }  

    /*--------------------------------------------------------------------*/
    private static void MostrarMenuAnalisis() {
        System.out.println("------Análisis de cotizaciones----------");
        System.out.println("2.1. Pedir valor ");
        System.out.println("2.2. Pedido avg");
        System.out.println("2.3. Pedir rent");
    }

    /*--------------------------------------------------------------------*/
    private static void RealizarPedidoValorAvg() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(">ejemplo");
        System.out.println(">avg 2019-02-26 2019-02-27 close ");
        System.out.println("---------------------------------------");
        
        System.out.println("> avg dataInicio dataFin campo");
        
        System.out.print("> ");
        String input = scanner.nextLine().trim();
        String[] parts = input.split(" ");
        if (parts.length != 4) {
            System.out.println("Formato incorrecto. Por favor, siga el formato especificado.");
            return;
            
        }

        String dataInicio = parts[1];
        String dataFin = parts[2];
        String campo = parts[3];

        double valorMedio = calcularValorMedio(dataInicio, dataFin, campo);
        if (valorMedio != -1) {
            System.out.println(valorMedio);
        } else {
            System.out.println("No se encontraron datos para las fechas especificadas o el campo indicado.");
        }
    }

    
    private static void RealizarAnalisisCotizaciones() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("2. Análisis de cotizaciones");
        System.out.println("2.2. Pedido avg");
        System.out.println("> avg dataInicio dataFin campo");
        System.out.print("> ");
        String input = scanner.nextLine().trim();
        String[] parts = input.split(" ");
        if (parts.length != 4) {
            System.out.println("Formato incorrecto. Por favor, siga el formato especificado.");
            return;
            
        }

        String dataInicio = parts[1];
        String dataFin = parts[2];
        String campo = parts[3];

        double valorMedio = calcularValorMedio(dataInicio, dataFin, campo);
        if (valorMedio != -1) {
            System.out.println(valorMedio);
        } else {
            System.out.println("No se encontraron datos para las fechas especificadas o el campo indicado.");
        }
    }

    /*--------------------------------------------------------------------*/
    private static double calcularValorMedio(String dataInicio, String dataFin, String campo) {
        double sum = 0;
        int count = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date inicio = dateFormat.parse(dataInicio);
            Date fin = dateFormat.parse(dataFin);
            for (String[] registro : cotizacionData) {
                Date fechaRegistro = dateFormat.parse(registro[0]);
                if (!fechaRegistro.before(inicio) && !fechaRegistro.after(fin)) {
                    switch (campo) {
                        case "open":
                            sum += Double.parseDouble(registro[1]);
                            break;
                        case "close":
                            sum += Double.parseDouble(registro[4]);
                            break;
                        case "high":
                            sum += Double.parseDouble(registro[2]);
                            break;
                        case "low":
                            sum += Double.parseDouble(registro[3]);
                            break;
                        case "volume":
                            sum += Double.parseDouble(registro[6]);
                            break;
                        default:
                            return -1; // Indicar error si el campo no es válido
                    }
                    count++;
                }
            }
            if (count == 0) {
                return -1; // Indicar error si no se encontraron datos para las fechas especificadas
            }
            return sum / count;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Indicar error si hay un problema al analizar las fechas
        }
    }
    /*--------------------------------------------------------------------*/
  private static void leerArchivoCotizaciones(File cotizacionFile) {
    try (BufferedReader br = new BufferedReader(new FileReader(cotizacionFile))) {
        String line;
        boolean headerSkipped = false;
        while ((line = br.readLine()) != null) {
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }

            String[] fields = line.split(",");
            cotizacionData.add(fields);
        }
    } catch (IOException e) {
        System.out.println("Error al leer el archivo: " + e.getMessage());
    }
}
    /*---------------------------------------------------------------------*/
    private static void cargarDatosCotizaciones(File cotizacionFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(cotizacionFile))) {
            String line;
            boolean headerSkipped = false; 
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
               
                String[] fields = line.split(",");
                cotizacionData.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*--------------------------------------------------------------------*/
    private static void cargarDatosCotizaciones() {
        String currentUsersHomeDir = System.getProperty("user.home");
        File cotizacionFile = new File(currentUsersHomeDir + "/BitcoinsManager/cotizaciones/BTC-USD.csv");
        if (!cotizacionFile.exists()) {
            System.out.println("No se encontró el archivo BTC-USD.csv en la carpeta 'cotizaciones'.");
            return;
        }
        System.out.println("Archivo BTC-USD.csv cargado.");
        leerArchivoCotizaciones(cotizacionFile);
    }
    
    /*------------------------------------------------------------------------*/
    private static void RealizarPedidoValor() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la fecha en formato yyyy-mm-dd: ");
        String fecha = scanner.next();
        System.out.print("Ingrese el campo (open, close, high, low, volume): ");
        String campo = scanner.next();

        boolean encontrado = false;
        for (String[] registro : cotizacionData) {
            if (registro[0].equals(fecha)) {
                switch (campo) {
                    case "open":
                        System.out.println("Valor de apertura en la fecha " + fecha + ": " + registro[1]);
                        break;
                    case "close":
                        System.out.println("Valor de cierre en la fecha " + fecha + ": " + registro[4]);
                        break;
                    case "high":
                        System.out.println("Valor máximo en la fecha " + fecha + ": " + registro[2]);
                        break;
                    case "low":
                        System.out.println("Valor mínimo en la fecha " + fecha + ": " + registro[3]);
                        break;
                    case "volume":
                        System.out.println("Volumen en la fecha " + fecha + ": " + registro[6]);
                        break;
                    default:
                        System.out.println("Campo inválido. Por favor, seleccione uno válido.");
                        return;
                }
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println("No se encontraron datos para la fecha especificada.");
        }
    }
}
