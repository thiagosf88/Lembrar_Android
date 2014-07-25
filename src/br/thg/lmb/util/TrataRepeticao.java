package br.thg.lmb.util;



public class TrataRepeticao {

	public static String trataRepeticao(String repeticaoXML) {
		String repeticao = "", frequencia, dias = "", intervalo;

		if (repeticaoXML != null && !repeticaoXML.trim().equals("")) {

			frequencia = retornaValores(repeticaoXML, "FREQ=");

			if (repeticaoXML.contains("BYDAY=")) {
				dias = retornaValores(repeticaoXML, "BYDAY=");
			}

			intervalo = retornaValores(repeticaoXML, "INTERVAL=");
			

			if (Integer.parseInt(intervalo) == 1) {

				if (frequencia.equals("DAILY"))
					repeticao = "diariamente";
				else if (frequencia.equals("WEEKLY")) {

					repeticao = "semanalmente";
					if (!dias.trim().equals(""))
						repeticao += formataDias(dias);
				} else if (frequencia.equals("MONTHLY"))
					repeticao = "mensalmente";
				else if (frequencia.equals("YEARLY"))
					repeticao = "anualmente";

			} else {

				if (frequencia.equals("DAILY"))
					repeticao = "a cada " + intervalo + " dias";
				else if (frequencia.equals("WEEKLY")) {

					repeticao = "a cada " + intervalo + " semanas";
					if (!dias.trim().equals(""))
						repeticao += formataDias(dias);
				} else if (frequencia.equals("MONTHLY"))
					repeticao = "a cada " + intervalo + " meses";
				else if (frequencia.equals("YEARLY"))
					repeticao = "a cada " + intervalo + " anos";

			}

		}
		return repeticao;
	}

	private static String formataDias(String dias) {
		String diasFormatados = " às", tmp = "";
		int indice = 0, indice2 = 0;
		while (indice < dias.length()) {
			indice2 = dias.indexOf(",", indice) > 0 ? dias.indexOf(",", indice)
					: dias.length();
			tmp = dias.substring(indice, indice2);

			if (tmp.equals("MO"))
				if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e segundas,";
				else
					diasFormatados += " segundas,";
			else if (tmp.equals("TU"))
				if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e terças,";
				else
					diasFormatados += " terças,";
			else if (tmp.equals("WE"))
				if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e quartas,";
				else
					diasFormatados += " quartas,";
			else if (tmp.equals("TH"))
				if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e quintas,";
				else
					diasFormatados += " quintas,";
			else if (tmp.equals("FR"))
				if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e sextas,";
				else
					diasFormatados += " sextas,";
			else if (tmp.equals("SA"))
				if (indice == 0)
					diasFormatados = " aos sábados,";
				else if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e sábados,";

				else
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " sábados,";
			else if (tmp.equals("SU"))
				if (indice == 0)
					diasFormatados = " aos domingos,";
				else if (indice2 + 1 >= dias.length())
					diasFormatados = diasFormatados.substring(0, diasFormatados
							.length() - 1)
							+ " e domingos,";

				else
					diasFormatados += " domingos,";

			indice = indice2 + 1;
		}

		return diasFormatados.substring(0, diasFormatados.length() - 1);
	}

	private static String retornaValores(String rep, String par) {
		String resultado = "";
		int indice = 0, indice2 = 0;
		
		indice = rep.indexOf(par) + par.length();
		indice2 = rep.indexOf(";", indice);
		if (indice2 < 0)
			indice2 = rep.length();
		
		resultado = rep.substring(indice, indice2);

		return resultado;
	}

}
