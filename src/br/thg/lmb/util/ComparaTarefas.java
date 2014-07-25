package br.thg.lmb.util;

import java.util.Comparator;

import br.thg.lmb.dominio.Tarefa;

public class ComparaTarefas implements Comparator<Tarefa> {

	@Override
	public int compare(Tarefa t1, Tarefa t2) {

		if ((t1.getDataPrevista() != null && t2.getDataPrevista() == null)
				|| (t1.getDataPrevista() != null
						&& t2.getDataPrevista() != null && t1.getDataPrevista()
						.before(t2.getDataPrevista())))
			return -1;

		if ((t2.getDataPrevista() != null && t1.getDataPrevista() == null)
				|| (t1.getDataPrevista() != null
						&& t2.getDataPrevista() != null && (t1
						.getDataPrevista().after(t2.getDataPrevista()))))
			return 1;

		if (t1.getDataPrevista() == null && t2.getDataPrevista() == null
				|| (t1.getDataPrevista().compareTo(t2.getDataPrevista()) == 0)) {

			if ((t1.getPrioridade() != null && !t1.getPrioridade()
					.equalsIgnoreCase("N"))
					&& (t2.getPrioridade() == null || t2.getPrioridade()
							.equalsIgnoreCase("N")))
				return -1;

			if ((t2.getPrioridade() != null && t2.getPrioridade()
					.equalsIgnoreCase("N"))
					&& (t1.getPrioridade() == null)
					|| t1.getPrioridade().equalsIgnoreCase("N"))
				return 1;

			if (Integer.parseInt(t1.getPrioridade()) < Integer.parseInt(t2
					.getPrioridade()))
				return -1;
			if (Integer.parseInt(t1.getPrioridade()) > Integer.parseInt(t2
					.getPrioridade()))
				return 1;
		}

		return 0;
	}

}
