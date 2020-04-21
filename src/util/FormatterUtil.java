package util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Wagner
 */
public class FormatterUtil {
    
    private static DecimalFormat decimalForma;

    public static String definirMascara(String value, String mask) {
        if (value == null) {
            return "";
        }
        try {
            MaskFormatter maskFormatter = new MaskFormatter(mask);
            maskFormatter.setValueContainsLiteralCharacters(false);
            String valueFormatado = maskFormatter.valueToString(value);
            return valueFormatado;
        } catch (ParseException ex) {
            return value;
        }
    }

    public static float getValorPago(String valorPago) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        try {
            Number parse;
            if (valorPago.contains(".") && valorPago.contains(",")) {
                parse = numberFormat.parse(valorPago);

            } else if (valorPago.contains(",")) {

                parse = numberFormat.parse(valorPago);
            } else {

                parse = Float.parseFloat(valorPago);
            }
            return parse.floatValue();
        } catch (ParseException ex) {
            return 0f;
        }
    }
    
    public static String getValorFormatado(double valor) {
        if(decimalForma == null){
           decimalForma = new DecimalFormat("###,##0.00");
        }
        return decimalForma.format(valor);
    }
    
}
