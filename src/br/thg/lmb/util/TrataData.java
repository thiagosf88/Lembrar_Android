package br.thg.lmb.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TrataData {

	public static GregorianCalendar trata(String dataDoXml) {
		GregorianCalendar data = null;
		int ano, mes, dia;

		if (dataDoXml != null && !dataDoXml.equals("")) {
			// Log.v("thiago", dataDoXml);
			ano = Integer.parseInt(dataDoXml.substring(0, 4));
			mes = Integer.parseInt(dataDoXml.substring(5, 7)) - 1;
			dia = Integer.parseInt(dataDoXml.substring(8, 10));

			data = new GregorianCalendar(ano, mes, dia);
		}

		return data;
	}

	public static String trataHora(String dataDoXml, float diferencaFuso) {
		if (dataDoXml != null && !dataDoXml.equals("")) {
			String hora, minuto;
			hora = dataDoXml.substring(11, 13);
			minuto = dataDoXml.substring(14, 16);

			int dif[] = trataDiferencaFuso(diferencaFuso);
			float tempM = Float.parseFloat(minuto);
			float tempH = Float.parseFloat(hora);
			float tempM2 = tempM + dif[1];
			tempM = Math.abs(tempM2) % 60;
			if (tempM2 < 0) {
				tempH--;
				tempM = 60 + tempM2;

			} else if (tempM2 >= 60) {

				tempH++;

			}
			minuto = tempM >= 10 ? String.valueOf((int) tempM) : "0"
					+ String.valueOf((int) tempM);
			tempH = tempH + dif[0];
			if (tempH >= 24)
				tempH = tempH - 24;
			tempH = tempH >= 0 ? tempH : 24 + tempH;

			hora = tempH >= 10 ? String.valueOf((int) tempH) : "0"
					+ String.valueOf((int) tempH);

			return hora + ":" + minuto;

		}

		return "00:00";
	}

	public static String[] horaProRTM(String horaDoLembrar, String dia,
			float diferencaFuso) {
		String[] dataHora = new String[2];
		String hora, minuto;
		float tz;
		int tz_trat[];

		tz = diferencaFuso <= 0 ? Math.abs(diferencaFuso) : 24 - diferencaFuso;

		tz_trat = trataDiferencaFuso(tz);
		dataHora[0] = dia;

		dataHora[1] = (tz_trat[0] >= 10 ? String.valueOf(tz_trat[0]) : "0"
				+ String.valueOf(tz_trat[0]))
				+ ":"
				+ (tz_trat[1] >= 10 ? String.valueOf(tz_trat[1]) : "0"
						+ String.valueOf(tz_trat[1]));

		if (horaDoLembrar != null && !horaDoLembrar.equals("")) {

			hora = horaDoLembrar.substring(0, 2);
			minuto = horaDoLembrar.substring(3, 5);

			int dif[] = trataDiferencaFuso(diferencaFuso);
			float tempM = Float.parseFloat(minuto);
			float tempH = Float.parseFloat(hora);
			float tempM2 = tempM - dif[1];
			tempM = Math.abs(tempM2) % 60;
			if ((tempM2) >= 60) {
				tempH++;
			} else if (tempM2 < 0) {
				tempH--;
				tempM = 60 - tempM;
			}
			minuto = tempM >= 10 ? String.valueOf((int) tempM) : "0"
					+ String.valueOf((int) tempM);
			tempH = tempH - dif[0];

			if (tempH > 23) {
				GregorianCalendar tmpG = trata(dia);
				
				tmpG.add(GregorianCalendar.DAY_OF_MONTH, 1);
				dia = trataGregorian(tmpG);
				tempH = tempH - 24;

				dataHora[0] = dia;
			} else if (tempH < 0) {
				GregorianCalendar tmpG = trata(dia);
				
				tmpG.add(GregorianCalendar.DAY_OF_MONTH, -1);
				dataHora[0] = trataGregorian(tmpG);
				tempH = 24 + tempH;

			}

			hora = tempH >= 10 ? String.valueOf((int) tempH) : "0"
					+ String.valueOf((int) tempH);
			dataHora[1] = hora + ":" + minuto;

		}

		return dataHora;
	}

	public static String trataGregorian(GregorianCalendar dt) {

		if (dt == null)
			return null;

		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

		return formato.format(dt.getTime());
	}

	public static String proRTM(String ddMMYYYY) {

		String ano, mes, dia;

		if (ddMMYYYY != null && !ddMMYYYY.equals("")) {

			dia = ddMMYYYY.substring(0, 2);
			mes = ddMMYYYY.substring(3, 5);
			ano = ddMMYYYY.substring(6, 10);

			return ano + "-" + mes + "-" + dia;
		}
		return "";
	}

	public static int[] trataDiferencaFuso(float diferencaFuso) {
		int[] horaMinuto = new int[2];
		String partFrac, frac;
		int ic;

		partFrac = String.valueOf(diferencaFuso);
		ic = partFrac.indexOf(".");
		if (ic > 0) {
			frac = partFrac.substring(ic + 1).length() == 1 ? partFrac
					.substring(ic + 1)
					+ "0" : partFrac.substring(ic + 1);

			horaMinuto[1] = (int) (Float.valueOf(frac) * 60) / 100;

			while (horaMinuto[1] >= 60) {
				diferencaFuso++;
				horaMinuto[1] = Math.abs(horaMinuto[1] - 60);
			}

		} else
			horaMinuto[1] = 0;
		horaMinuto[0] = (int) Math.floor((double) diferencaFuso);

		return horaMinuto;
	}
	
	public static float deHoraParaFloat(String hora){
		float resultado = 0;
		
		String frac;
		int ic;

		ic = hora.indexOf(":");
		if (ic > 0) {
			frac =  hora
					.substring(ic + 1);					

			resultado =  (Float.valueOf(frac) / 60);
			resultado += Float.valueOf(hora.substring(0, ic));
		}
		return resultado;
	}

}
