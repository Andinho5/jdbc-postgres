import java.io.*;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        connect();
        //The rest is coming soon
    }

    private static void connect() {
        String query = "select * from cars";
        try {
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "suserLel98!?");
            Statement statement = con.createStatement();
            ResultSet rsCnt = statement.executeQuery("select count(*) from cars;");
            rsCnt.next();
            int cnt = Integer.parseInt(rsCnt.getString(1));

            //System.out.println(cnt);
            ResultSet rs = statement.executeQuery(query);

            ResultSetMetaData rsmd = rs.getMetaData();
            String[] columns = new String[rsmd.getColumnCount()];
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columns[i - 1] = rsmd.getColumnName(i);
            }
            System.out.println(Arrays.toString(columns));
            ArrayList<String[]> arrData = new ArrayList<>();
            for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                String[] testData = new String[cnt];
                int j = 0;
                while (rs.next()) {
                    String columnData = rs.getString(i);
                    testData[j++] = columnData;
                }
                arrData.add(testData);

                rs = statement.executeQuery(query);
            }
            try (BufferedWriter w = new BufferedWriter(new FileWriter("test.html"))) {
                w.write("<!DOCTYPE html>\n" + "<html lang=\"\">\n" + "<style>\n" +
                        "\ttable, th, td {\n" + "\t  border:1px solid black;\n" + "\t}\n" + "</style>\n" + "<body>\n");

                w.write("<table style=\"width:100%\">\n");
                w.write("\t<tr>\n");
                for (String column : columns) {
                    w.write("\t\t<th>" + column + "</th>\n");
                }
                w.write("\t</tr>\n");
                for (int i = 0; i < arrData.get(0).length; i++) {
                    String[] input = new String[rsmd.getColumnCount()];
                    int j = 0;
                    for (String[] arr : arrData) {
                        input[j++] = arr[i];
                    }
                    //System.out.println(Arrays.toString(input));
                    w.write("\t<tr>\n");
                    for (int k = 0; k < input.length; k++) {
                        w.write("\t\t<td>" + input[k] + "</td>\n");
                    }
                    w.write("\t</tr>\n");
                }
                w.write("</table>" + "\n</body>" + "\n</html>");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeXML(ArrayList<String[]> arrData, ResultSetMetaData rsmd, String[] columns) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter("test.xml"))) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n");
            for (int i = 0; i < arrData.get(0).length; i++) {
                String[] input = new String[rsmd.getColumnCount()];
                int j = 0;
                for (String[] arr : arrData) {
                    input[j++] = arr[i];
                }
                System.out.println(Arrays.toString(input));
                assert input.length == columns.length;

                w.write("\t<car>\n");
                for (int k = 0; k < columns.length; k++) {
                    //w.write(columns[k] + " : " + input[k] + "\n");
                    w.write("\t\t<" + columns[k] + ">" + input[k] + "</" + columns[k] + ">\n");
                }
                w.write("\t</car>\n");
            }
            w.write("\n</root>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}