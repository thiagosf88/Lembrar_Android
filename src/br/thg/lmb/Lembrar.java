package br.thg.lmb;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import br.thg.lmb.dominio.Lista;
import br.thg.lmb.dominio.Nota;
import br.thg.lmb.dominio.Tarefa;
import br.thg.lmb.util.Md5;
import br.thg.lmb.util.TrataData;
import br.thg.lmb.util.TrataRepeticao;

public class Lembrar {

	String frobFinal;

	public boolean completarTarefa(String auth_token, String id,
			String idSeries, String lista) {

		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.completetask_id" + id
				+ "taskseries_id" + idSeries + "timeline" + timeline);
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.complete"
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		} else {

			return false;
		}

	}

	public boolean adiarTarefa(String auth_token, String id, String idSeries,
			String lista) {

		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.postponetask_id" + id
				+ "taskseries_id" + idSeries + "timeline" + timeline);
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.postpone"
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		} else {

			return false;
		}

	}

	public boolean deletarTarefa(String auth_token, String id, String idSeries,
			String lista) {

		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.deletetask_id" + id
				+ "taskseries_id" + idSeries + "timeline" + timeline);
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.delete"
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		} else {

			return false;
		}

	}

	public boolean alterarTexto(String auth_token, String id, String idSeries,
			String lista, String texto) {
		if (texto == null || texto.equals(""))
			return false;
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.setNamename" + texto
				+ "task_id" + id + "taskseries_id" + idSeries + "timeline"
				+ timeline;
		api_sig = Md5.md5(tmp);

		texto = URLEncoder.encode(texto);

		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.setName"
				+ "&name="
				+ texto
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id=" + id + "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean inserirTags(String auth_token, String id, String idSeries,
			String lista, String tags) {
		if (tags == null)
			return false;
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.setTagstags" + tags
				+ "task_id" + id + "taskseries_id" + idSeries + "timeline"
				+ timeline;
		api_sig = Md5.md5(tmp);
		tags = URLEncoder.encode(tags);
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.setTags"
				+ "&tags="
				+ tags
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id=" + id + "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean inserirPrioridade(String auth_token, String id,
			String idSeries, String lista, String prioridade) {
		if (prioridade == null || prioridade.equals(""))
			return false;
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.setPrioritypriority"
				+ prioridade + "task_id" + id + "taskseries_id" + idSeries
				+ "timeline" + timeline;
		api_sig = Md5.md5(tmp);

		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.setPriority"
				+ "&priority="
				+ prioridade
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean inserirUrl(String auth_token, String id, String idSeries,
			String lista, String url) {
		String termoURL = "url", paramURL = "&url=";
		if (url == null) {
			url = "";
			termoURL = "";
			paramURL = "";
		}else{
			if (!url.contains("://")) {
				url = "http://" + url;
			}
		}

		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.setURL" + "task_id" + id
				+ "taskseries_id" + idSeries + "timeline" + timeline + termoURL
				+ url;
		api_sig = Md5.md5(tmp);

		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.setURL"
				+ paramURL
				+ url
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id=" + id + "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean inserirLimite(String auth_token, String id, String idSeries,
			String lista, String data, String hora, float diferencaFuso) {

		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;
		float tz;
		int tz_trat[];
		tz = diferencaFuso <= 0 ? Math.abs(diferencaFuso) : 24 - diferencaFuso;
		String parse = "parse1";
		tz_trat = TrataData.trataDiferencaFuso(tz);
		String temHora = "0";
		String partTime = (tz_trat[0] >= 10 ? String.valueOf(tz_trat[0]) : "0"
				+ String.valueOf(tz_trat[0]))
				+ ":"
				+ (tz_trat[1] >= 10 ? String.valueOf(tz_trat[1]) : "0"
						+ String.valueOf(tz_trat[1]));
		String timezone = "T" + partTime + ":00Z";

		String dataHora[] = TrataData.horaProRTM(hora, data, diferencaFuso);
		String due = "due";
		String has_due_time = "has_due_time";

		data = dataHora[0];
		hora = dataHora[1];
		if (hora != null && !hora.trim().equals("")) {
			if (!hora.trim().equals(partTime)) {
				temHora = "1";
				parse = "";
				timezone = "T" + hora.trim() + ":00Z";
			} else
				timezone = "";
		}
		Log.v("dt", data + " " + timezone);
		if (data == null || data.equals("")) {
			due = temHora = timezone = has_due_time = hora = "";

		}

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + due + data
				+ timezone + "formatxml" + has_due_time + temHora + "list_id"
				+ lista + "methodrtm.tasks.setDueDate" + parse + "task_id" + id
				+ "taskseries_id" + idSeries + "timeline" + timeline;
		api_sig = Md5.md5(tmp);
		timezone = timezone.replace(":", "%3A");
		if (data != null && !data.trim().equals("")) {
			due = "&due=";
			has_due_time = "&has_due_time=";

		}
		if (temHora.equals("1"))
			parse = "";
		else {
			parse = "&parse=1";
		}
		String urlString = "https://www.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.setDueDate"
				+ due
				+ data
				+ timezone
				+ has_due_time
				+ temHora
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline + parse;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean adicionarTags(String auth_token, String id, String idSeries,
			String lista, String tags) {
		if (tags == null || tags.equals(""))
			return false;
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.addTagstags" + tags
				+ "task_id" + id + "taskseries_id" + idSeries + "timeline"
				+ timeline;
		api_sig = Md5.md5(tmp);
		tags = URLEncoder.encode(tags);
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.addTags"
				+ "&tags="
				+ tags
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id=" + id + "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean adicionarRepeticao(String auth_token, String id,
			String idSeries, String lista, String repeticao) {
		if (repeticao == null || repeticao.equals(""))
			return false;
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "list_id" + lista + "methodrtm.tasks.setRecurrencerepeat"
				+ repeticao + "task_id" + id + "taskseries_id" + idSeries
				+ "timeline" + timeline;
		api_sig = Md5.md5(tmp);
		repeticao = URLEncoder.encode(repeticao);
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.setRecurrence"
				+ "&repeat="
				+ repeticao
				+ "&list_id="
				+ lista
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		}
		return false;

	}

	public boolean MudaDeLista(String auth_token, String id, String idSeries,
			String listaOrigem, String listaDestino) {

		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return false;

		String api_sig = "", tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ "from_list_id" + listaOrigem + "methodrtm.tasks.moveTo"
				+ "task_id" + id + "taskseries_id" + idSeries + "timeline"
				+ timeline + "to_list_id" + listaDestino;
		api_sig = Md5.md5(tmp);

		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&format=xml"
				+ "&method=rtm.tasks.moveTo"
				+ "&to_list_id="
				+ listaDestino
				+ "&from_list_id="
				+ listaOrigem
				+ "&taskseries_id="
				+ idSeries
				+ "&task_id="
				+ id
				+ "&timeline=" + timeline;

		Document d = getDocument(urlString);
		if (d == null)
			return false;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		Node node = nodeLst.item(0);
		Element pai = null;

		pai = (Element) node;
		if (pai.getAttribute("stat").equals("ok")) {
			return true;
		} else {

			return false;
		}

	}

	public HashMap<String, String> adicionaTarefaComSmart(String auth_token,
			String nome, String lista, String tags, String prioridade,
			String repeticao, String limite, String hora, String url) {

		String linhaLista = "list_id", lLista = "&list_id";
		HashMap<String, String> tarefaAdicionada = new HashMap<String, String>();
		if (lista != null && !lista.equals("")) {
			linhaLista += lista;
			lLista += "=" + lista;
		} else
			linhaLista = "";
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return null;

		prioridade = (prioridade != null && !prioridade.trim().equals("") && !prioridade
				.trim().equalsIgnoreCase("N")) ? " !" + prioridade : "";
		repeticao = (repeticao != null && !repeticao.trim().equals("")) ? " *"
				+ repeticao : "";
		limite = (limite != null && !limite.trim().equals("")) ? " ^" + limite
				: "";
		hora = (hora != null && !hora.trim().equals("")) ? " at " + hora + "hs"
				: "";
		tags = tags.replace(",", " ");
		tags = tags.replaceAll("\\s+", " ");
		tags = tags.replaceAll("^\\s+", "");

		if (tags.length() >= 1 && tags.charAt(tags.length() - 1) == ' ')
			tags = new String(tags.substring(0, tags.length() - 1));

		tags = (tags != null && !tags.trim().equals("")) ? (" " + tags)
				.replace(" ", " #") : "";

		if (!url.trim().equals("") && !url.contains("://")) {
			url = " http://" + url;
		}

		nome += prioridade + repeticao + limite + hora + tags + url;

		String api_sig = "";
		String tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ linhaLista + "methodrtm.tasks.addname" + nome + "parse1"
				+ "timeline" + timeline;
		api_sig = Md5.md5(tmp);

		nome = URLEncoder.encode(nome);

		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&name="
				+ nome
				+ lLista
				+ "&timeline="
				+ timeline
				+ "&method=rtm.tasks.add&format=xml&parse=1";

		Document d = getDocument(urlString);
		if (d == null)
			return null;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				Element transacao = (Element) pai.getFirstChild();
				Element list = (Element) transacao.getNextSibling();

				tarefaAdicionada.put("lista", list.getAttribute("id"));

				Element tarefa = (Element) list.getFirstChild();

				tarefaAdicionada.put("idSeries", tarefa.getAttribute("id"));
				tarefaAdicionada.put("id", ((Element) tarefa
						.getElementsByTagName("task").item(0))
						.getAttribute("id"));

			} else {

				return null;
			}
		}

		return tarefaAdicionada;
	}

	public HashMap<String, String> adicionaTarefa(String auth_token,
			String nome, String lista) {

		String linhaLista = "list_id", lLista = "&list_id";
		HashMap<String, String> tarefaAdicionada = new HashMap<String, String>();
		if (lista != null && !lista.equals("")) {
			linhaLista += lista;
			lLista += "=" + lista;
		} else
			linhaLista = "";
		String timeline = criaTimeline(auth_token);
		if (timeline == null)
			return null;

		String api_sig = "";
		String tmp = "f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token + "formatxml"
				+ linhaLista + "methodrtm.tasks.addname" + nome + "timeline"
				+ timeline;
		api_sig = Md5.md5(tmp);

		nome = URLEncoder.encode(nome);

		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&name="
				+ nome
				+ lLista
				+ "&timeline="
				+ timeline
				+ "&method=rtm.tasks.add&format=xml";

		Document d = getDocument(urlString);
		if (d == null)
			return null;
		NodeList nodeLst = d.getElementsByTagName("rsp");

		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				Element transacao = (Element) pai.getFirstChild();
				Element list = (Element) transacao.getNextSibling();

				tarefaAdicionada.put("lista", list.getAttribute("id"));

				Element tarefa = (Element) list.getFirstChild();

				tarefaAdicionada.put("idSeries", tarefa.getAttribute("id"));
				tarefaAdicionada.put("id", ((Element) tarefa
						.getElementsByTagName("task").item(0))
						.getAttribute("id"));

			} else {

				return null;
			}
		}

		return tarefaAdicionada;
	}

	public ArrayList<Tarefa> todasAsTarefas(String auth_token,
			float diferencaFuso) {
		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token
				+ "filterstatus:incompleteformatxmlmethodrtm.tasks.getList");
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&filter=status%3Aincomplete&method=rtm.tasks.getList&format=xml";

		Document d = getDocument(urlString);
		if (d == null)
			return null;
		NodeList nodeLst = d.getElementsByTagName("rsp");
		ArrayList<Tarefa> tarefas = new ArrayList<Tarefa>();
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				Element tasks = (Element) pai.getFirstChild();

				// tasks.getFirstChild() = list
				int qntListas = tasks.getChildNodes().getLength(), qntTarefasLista = 0, qntTarefasRepeticao = 1;
				Element list = null, tarefaLista = null;

				Tarefa tmp = null, tmpR = null;
				Long idLista = null;
				ArrayList<String> tags = null;
				ArrayList<Nota> notas = null;
				Boolean temhora = false;
				for (int jn = 0; jn < qntListas; jn++) {

					list = (Element) tasks.getChildNodes().item(jn);
					idLista = Long.valueOf(list.getAttribute("id"));
					qntTarefasLista = list.getChildNodes().getLength();

					for (int k = 0; k < qntTarefasLista; k++) {
						temhora = false;
						tmp = new Tarefa();
						tarefaLista = (Element) list.getChildNodes().item(k);
						tmp.setIdSeries(Long.valueOf(tarefaLista
								.getAttribute("id")));
						tmp.setName(tarefaLista.getAttribute("name"));

						// Pegando dados da URL
						tmp.setUrl(tarefaLista.getAttribute("url"));

						int tm = (tarefaLista).getElementsByTagName("tag")
								.getLength();

						tags = new ArrayList<String>();

						for (int n = 0; n < tm; n++) {

							tags.add(new String((tarefaLista)
									.getElementsByTagName("tag").item(n)
									.getChildNodes().item(0).getNodeValue()));
						}

						// _________________________________________________________
						int tn = (tarefaLista).getElementsByTagName("note")
								.getLength();
						
						notas = new ArrayList<Nota>();
						Nota nt = null;
						for (int n1 = 0; n1 < tn; n1++) {
							nt = new Nota();

							nt.setId(Long.valueOf(((Element) (tarefaLista
									.getElementsByTagName("note").item(n1)))
									.getAttribute("id")));
							nt.setTitulo(((Element) (tarefaLista
									.getElementsByTagName("note").item(n1)))
									.getAttribute("title"));
							nt.setNota(new String((tarefaLista)
									.getElementsByTagName("note").item(n1)
									.getChildNodes().item(0).getNodeValue()));
							notas.add(nt);
							
							nt = null;
						}
						tmp.setNotas(notas);
						
						qntTarefasRepeticao = (tarefaLista)
								.getElementsByTagName("task").getLength();
						if ((tarefaLista).getElementsByTagName("rrule").item(0) != null) {

							tmp.setRepeticao(TrataRepeticao
									.trataRepeticao((tarefaLista)
											.getElementsByTagName("rrule")
											.item(0).getChildNodes().item(0)
											.getNodeValue()));
						}

						tmp.setPrioridade(((Element) (tarefaLista)
								.getElementsByTagName("task").item(0))
								.getAttribute("priority"));
						if (((Element) (tarefaLista).getElementsByTagName(
								"task").item(0)).getAttribute("has_due_time")
								.equals("1")) {
							temhora = true;
							tmp.setHoraPrevista(TrataData.trataHora(
									((Element) (tarefaLista)
											.getElementsByTagName("task").item(
													0)).getAttribute("due"),
									diferencaFuso));
						} else {
							tmp.setHoraPrevista(null);
							temhora = false;
						}

						tmp.setLista(idLista.toString());

						tmp.setCompletaEm(TrataData
								.trata(((Element) (tarefaLista)
										.getElementsByTagName("task").item(0))
										.getAttribute("completed")));

						tmp.setTags(tags);
						tmpR = new Tarefa(tmp);
						for (int rp = 0; rp < qntTarefasRepeticao; rp++) {

							tmp.setDataPrevista(TrataData
									.trata(((Element) (tarefaLista)
											.getElementsByTagName("task").item(
													rp)).getAttribute("due")));

							if (diferencaFuso < 0
									&& (tmp.getHoraPrevista() != null)
									&& TrataData.deHoraParaFloat(tmp
											.getHoraPrevista()) > (24 + diferencaFuso)
									&& (tmp.getDataPrevista() != null)) {

								tmp.getDataPrevista().add(
										GregorianCalendar.DAY_OF_MONTH, -1);

							} else if (diferencaFuso > 0
									&& (tmp.getHoraPrevista() != null)
									&& TrataData.deHoraParaFloat(tmp
											.getHoraPrevista()) < diferencaFuso
									&& (tmp.getDataPrevista() != null)
									&& temhora) {

								tmp.getDataPrevista().add(
										GregorianCalendar.DAY_OF_MONTH, 1);
							}

							tmp.setId(Long.valueOf(((Element) (tarefaLista)
									.getElementsByTagName("task").item(rp))
									.getAttribute("id")));
							tarefas.add(tmp);
							tmp = null;
							tmp = tmpR;
						}

					}

				}

			} else {

				return null;
			}
		}

		return tarefas;
	}

	public ArrayList<Lista> getListas(String auth_token) {
		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token
				+ "methodrtm.lists.getList");
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key=7ccb7b5d11fca89bb281e092211ed3a6&&auth_token="
				+ auth_token + "&method=rtm.lists.getList";

		Document d = getDocument(urlString);
		if (d == null)
			return null;
		ArrayList<Lista> listas = new ArrayList<Lista>();
		NodeList nodeLst = d.getElementsByTagName("rsp");
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				int tm = (pai).getElementsByTagName("list").getLength();

				for (int m = 0; m < tm; m++) {
					listas.add(new Lista(Long.valueOf(((Element) pai
							.getElementsByTagName("list").item(m))
							.getAttribute("id")), ((Element) pai
							.getElementsByTagName("list").item(m))
							.getAttribute("name")));

				}

			} else {

				return null;

			}
		}
		return listas;
	}

	public String criaTimeline(String auth_token) {
		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token
				+ "formatxmlmethodrtm.timelines.create");
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&method=rtm.timelines.create&format=xml";

		Document d = getDocument(urlString);

		if (d == null)
			return null;

		NodeList nodeLst = d.getElementsByTagName("rsp");
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				return ((Element) pai.getElementsByTagName("timeline").item(0))
						.getChildNodes().item(0).getNodeValue();
			}
			/*
			 * else { //filho = pai.getChildNodes().item(0); // while(pai.) //
			 * pega o segundo atributo desse elemento //return
			 * ((Element)filho.getAttributes().item(1)).getAttribute("code");
			 * return null; }
			 */
		}
		return null;
	}

	/*
	 * Método ainda sem uso mas a principio não deve ser apagado public void
	 * checkToken() { String api_sig = ""; api_sig =
	 * Md5.md5("api_key7ccb7b5d11fc" +
	 * "a89bb281e092211ed3a6methodrtm.auth.checkToken"); String urlString =
	 * "http://api.rememberthemilk.com/services/rest/?api_sig=" + api_sig +
	 * "&api_key=" + "7ccb7b5d11fca89bb281e092211ed3a6&auth_token=" +
	 * "d571422a1eb670d52222ec00d190814cb83b9580&method=rtm.auth.checkToken";
	 * 
	 * }
	 */

	private String obtemTimezone(String auth_token) {
		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token
				+ "formatxmlmethodrtm.settings.getList");
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&method=rtm.settings.getList&format=xml";

		Document d = getDocument(urlString);

		if (d == null)
			return null;

		NodeList nodeLst = d.getElementsByTagName("rsp");
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				return ((Element) pai.getElementsByTagName("timezone").item(0))
						.getChildNodes().item(0).getNodeValue();
			}
			/*
			 * else { //filho = pai.getChildNodes().item(0); // while(pai.) //
			 * pega o segundo atributo desse elemento //return
			 * ((Element)filho.getAttributes().item(1)).getAttribute("code");
			 * return null; }
			 */
		}
		return null;
	}

	public float obtemDiferencaFuso(String auth_token) {

		String minhaZona = obtemTimezone(auth_token);
		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6auth_token" + auth_token
				+ "formatxmlmethodrtm.timezones.getList");
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&auth_token="
				+ auth_token
				+ "&method=rtm.timezones.getList&format=xml";

		Document d = getDocument(urlString);

		if (d == null)
			return 0;

		NodeList nodeLst = d.getElementsByTagName("rsp"), timezones;
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null, tz;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				timezones = pai.getElementsByTagName("timezone");
				int numTz = timezones.getLength();
				for (int k = 0; k < numTz; k++) {

					tz = (Element) timezones.item(k);
					if (tz.getAttribute("name").equals(minhaZona)) {
						try {
							float diferenca = Float.parseFloat(tz
									.getAttribute("current_offset"))
									/ (float) 3600;
							return diferenca;
						} catch (Exception e) {
							return 0;
						}
					}

				}
			}
		}
		return 0;
	}

	public String getToken() {

		if (frobFinal == null || frobFinal.trim().equals(""))
			return "";

		String api_sig = "";
		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6frob" + frobFinal
				+ "methodrtm.auth.getToken");
		String urlString = "https://api.rememberthemilk.com/services/rest/?api_sig="
				+ api_sig
				+ "&api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&frob="
				+ frobFinal
				+ "&method=rtm.auth.getToken";

		Document d = getDocument(urlString);

		NodeList nodeLst = d.getElementsByTagName("rsp");
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i);
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				return ((Element) pai.getElementsByTagName("token").item(0))
						.getChildNodes().item(0).getNodeValue();

			}
		}

		return "";

	}

	public String autentica() {
		String frob = getFrob();
		String api_sig = "";

		api_sig = Md5.md5("f053f44f05078ac1api_key7ccb7b5d11fc"
				+ "a89bb281e092211ed3a6frob" + frob + "permsdelete");
		String urlString = "http://api.rememberthemilk.com/services/auth/?api_key="
				+ "7ccb7b5d11fca89bb281e092211ed3a6&perms=delete&frob="
				+ frob
				+ "&api_sig=" + api_sig;
		return urlString;

	}

	public String getFrob() {
		String md5 = "";

		md5 = Md5
				.md5("f053f44f05078ac1api_key7ccb7b5d11fca89bb281e092211ed3a6methodrtm.auth.getFrob");

		String frob = null;
		String urlString = "https://api.rememberthemilk.com/services/rest/"
				+ "?api_sig=" + md5 + "&api_key=7ccb7b5d"
				+ "11fca89bb281e092211ed3a6&method=rtm.auth.getFrob";

		Document d = getDocument(urlString);
		if (d == null)
			return "";
		NodeList nodeLst = d.getElementsByTagName("rsp");
		for (int i = 0; i < nodeLst.getLength(); i++) {

			Node node = nodeLst.item(i), filho;
			Element pai = null;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				pai = (Element) node;
			if (pai.getAttribute("stat").equals("ok")) {

				filho = pai.getChildNodes().item(0);
				frob = filho.getChildNodes().item(0).getNodeValue();
				frobFinal = frob;

			} else {

				frobFinal = null;
			}
		}
		return frob;
	}

	public Document getDocument(String urlString) {
		URL url = null;
		Document doc = null;
		HttpsURLConnection urlConnection = null;
		InputStream in = null;
		try {

			url = new URL(urlString);

			urlConnection = (HttpsURLConnection) url.openConnection();
			// urlConnection.setRequestProperty("Accept-Charset", "ISO-8859-1");
			urlConnection.setRequestProperty("Request-Method", "GET");
			// Log.v("set", urlConnection.getContentEncoding());
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.connect();
			in = new BufferedInputStream(urlConnection.getInputStream());

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			doc = db.parse(in);

			doc.getDocumentElement().normalize();

		} catch (Exception e) {

			e.printStackTrace();

			return null;
		} finally {
			urlConnection.disconnect();
		}
		return doc;
	}

}
