/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package br.thg.lmb.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import br.thg.lmb.dominio.Lista;
import br.thg.lmb.dominio.Nota;
import br.thg.lmb.dominio.Operacao;
import br.thg.lmb.dominio.Tarefa;
import br.thg.lmb.util.TrataData;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class TarefasDbAdapter {

	public static final String CHAVE_IDSERIES = "_idSeries";
	private static final String DATABASE_TB_TAREFAS = "tarefas";
	private static final String DATABASE_TB_TAREFAS_TEMP = "tarefasTemporarias";
	private static final String DATABASE_TB_LISTA = "listas";
	private static final String DATABASE_TB_TAGS = "tags";
	private static final String DATABASE_TB_TAGS_TEMP = "tagsTemporarias";
	private static final String DATABASE_TB_OPERACOES = "operacoes";
	private static final String DATABASE_TB_CONFIGURACOES = "configuracoes";
	private static final String DATABASE_TB_NOTAS = "notas";
	private static final String DATABASE_TB_NOTAS_TEMP = "notasTemporarias";
	public static final String CHAVE_NOME_LISTA = "nome";
	public static final String CHAVE_ID = "_id";
	public static final String CHAVE_IDNOTA = "_idNota";
	public static final String CHAVE_NAME = "name";
	public static final String CHAVE_LISTA = "list";
	public static final String CHAVE_TAGS = "tag";
	public static final String CHAVE_PRIO = "prioridade";
	public static final String CHAVE_DATAPREVISTA = "data_prevista";
	public static final String CHAVE_HORAPREVISTA = "hora_prevista";
	public static final String CHAVE_TIPO = "tipo";
	public static final String CHAVE_REPETICAO = "repeticao";
	public static final String CHAVE_URL = "url";
	public static final String CHAVE_TITULO = "titulo";
	public static final String CHAVE_NOTA = "nota";
	public static final String CHAVE_AUTH = "auth_token";
	public static final String CHAVE_ATUALIZA_INICIO = "atualizarAoIniciar";
	public static final String CHAVE_CAPA = "capa";
	private static final String TAG = "TarefasDbAdapter";
	public Cursor cnf;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_TB_TAREFAS = "create table "
			+ DATABASE_TB_TAREFAS
			+ "(_id integer primary key, "
			+ "_idSeries integer not null, name text not null, list text not null, "
			+ CHAVE_PRIO + " text not null, " + CHAVE_DATAPREVISTA + " date, "
			+ CHAVE_HORAPREVISTA + " text, " + CHAVE_REPETICAO + " text, "
			+ CHAVE_URL + " text);";
	private static final String DATABASE_CREATE_TB_TAREFAS_TEMP = DATABASE_CREATE_TB_TAREFAS
			.replace(DATABASE_TB_TAREFAS, DATABASE_TB_TAREFAS_TEMP);
	private static final String DATABASE_CREATE_TB_LISTAS = "create table "
			+ DATABASE_TB_LISTA + " (" + CHAVE_ID + " integer primary key, "
			+ CHAVE_NOME_LISTA + " text not null);";
	private static final String DATABASE_CREATE_TB_TAGS = "create table "
			+ DATABASE_TB_TAGS + " (" + CHAVE_ID + " integer not null, "
			+ CHAVE_TAGS + " text not null, " + CHAVE_IDSERIES
			+ " integer not null, PRIMARY KEY ( " + CHAVE_ID + ","
			+ CHAVE_IDSERIES + "," + CHAVE_TAGS + "));";

	private static final String DATABASE_CREATE_TB_TAGS_TEMP = DATABASE_CREATE_TB_TAGS
			.replace(DATABASE_TB_TAGS, DATABASE_TB_TAGS_TEMP);

	private static final String DATABASE_CREATE_TB_OPERACOES = "create table "
			+ DATABASE_TB_OPERACOES + " (" + CHAVE_ID + " integer not null, "
			+ CHAVE_TIPO + " integer not null, " + CHAVE_IDSERIES
			+ " integer not null, " + CHAVE_NOME_LISTA
			+ " text not null, PRIMARY KEY ( " + CHAVE_ID + ","
			+ CHAVE_IDSERIES + "," + CHAVE_TIPO + "));";

	private static final String DATABASE_CREATE_TB_CONFIGURACOES = "create table "
			+ DATABASE_TB_CONFIGURACOES
			+ " ( "
			+ CHAVE_AUTH
			+ " text not null, "
			+ CHAVE_ATUALIZA_INICIO
			+ " boolean, "
			+ CHAVE_CAPA + " integer )";

	private static final String DATABASE_CREATE_TB_NOTAS = "create table "
			+ DATABASE_TB_NOTAS + " ( " + CHAVE_ID + " integer not null, "
			+ CHAVE_IDNOTA + " integer not null, " + CHAVE_NOTA
			+ " text not null, " + CHAVE_TITULO + " text, PRIMARY KEY ( "
			+ CHAVE_ID + "," + CHAVE_IDNOTA + "));";

	private static final String DATABASE_CREATE_TB_NOTAS_TEMP = DATABASE_CREATE_TB_NOTAS
			.replace(DATABASE_TB_NOTAS, DATABASE_TB_NOTAS_TEMP);

	private static final String DATABASE_NAME = "lembrar";
	private static final int DATABASE_VERSION = 15; // vers�o ser� a 15

	private final Context mCtx;

	private boolean aberto = false;

	public boolean isAberto() {
		return aberto;
	}

	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		/*
		 * @Override public void onOpen(SQLiteDatabase db) { super.onOpen(db);
		 * 
		 * if (!db.isReadOnly()) { db.execSQL("PRAGMA foreign_keys=ON;"); } }
		 */

		DatabaseHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE_TB_TAREFAS);
			db.execSQL(DATABASE_CREATE_TB_TAREFAS_TEMP);
			db.execSQL(DATABASE_CREATE_TB_LISTAS);
			db.execSQL(DATABASE_CREATE_TB_TAGS);
			db.execSQL(DATABASE_CREATE_TB_TAGS_TEMP);
			db.execSQL(DATABASE_CREATE_TB_NOTAS);
			db.execSQL(DATABASE_CREATE_TB_NOTAS_TEMP);
			db.execSQL(DATABASE_CREATE_TB_OPERACOES);
			db.execSQL(DATABASE_CREATE_TB_CONFIGURACOES);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String bkpAuthToken = "";
			Log.w(TAG,
					"We are updating database from versiom: "
							+ oldVersion
							+ " to : "
							+ newVersion
							+ ", this action will erase all previous data, except the token!");

			Cursor tmp = db.query(false, DATABASE_TB_CONFIGURACOES,
					new String[] { CHAVE_AUTH }, null, null, null, null, null,
					null);
			if (!tmp.isClosed() && tmp.getCount() > 0) {
				tmp.moveToFirst();
				bkpAuthToken = tmp.getString(tmp
						.getColumnIndexOrThrow(CHAVE_AUTH));
				tmp.close();
				tmp = null;
			} else if (!tmp.isClosed()) {

				tmp.close();

				tmp = null;
			}
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_TAREFAS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_TAREFAS_TEMP);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_LISTA);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_TAGS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_TAGS_TEMP);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_NOTAS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_NOTAS_TEMP);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_OPERACOES);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB_CONFIGURACOES);

			onCreate(db);
			if (bkpAuthToken.trim().isEmpty()) {
				ContentValues initialValues = new ContentValues();
				initialValues.put(CHAVE_AUTH, bkpAuthToken);
				db.insert(DATABASE_TB_CONFIGURACOES, null, initialValues);
			}

		}

	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public TarefasDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public void apagaTudo() {

		deletaTodasTarefas();
		deletaTodasTarefasTemporarias();
		deletaTodasTags();
		deletaTodasTagsTemporarias();
		deletaTodasOperacoes();
		deletaTodasListas();
		deletaTodasNotas();
		deletaConfiguracoes();

	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */

	long insereConfiguracoes(SQLiteDatabase bd, Cursor cnf) {

		if (cnf == null || cnf.isClosed())
			return 0;
		Log.v("Cursor", String.valueOf(cnf.getCount()));
		// Log.v("ab", String.valueOf(bd.));
		cnf.moveToPosition(0);

		// The Cursor is now set to the right position

		String auth_token = cnf
				.getString(cnf.getColumnIndexOrThrow(CHAVE_AUTH));

		ContentValues initialValues = new ContentValues();

		initialValues.put(CHAVE_AUTH, auth_token);
		// initialValues.put(CHAVE_ATUALIZA_INICIO, atualizaInicio);

		return bd.insert(DATABASE_TB_CONFIGURACOES, null, initialValues);
	}

	Cursor getConfiguracoesAntigas(SQLiteDatabase bd) {

		return bd.query(false, DATABASE_TB_TAREFAS, new String[] { CHAVE_ID },
				null, null, null, null, null, null);
	}

	public Bundle getConfiguracoes() {
		Bundle cnf = new Bundle();
		Cursor tmp = mDb.query(false, DATABASE_TB_CONFIGURACOES, new String[] {
				CHAVE_AUTH, CHAVE_ATUALIZA_INICIO, CHAVE_CAPA }, null, null,
				null, null, null, null);

		if (!tmp.isClosed() && tmp.getCount() > 0) {
			tmp.moveToFirst();
			cnf.putString(CHAVE_AUTH,
					tmp.getString(tmp.getColumnIndexOrThrow(CHAVE_AUTH)));
			if (!tmp.isNull(tmp.getColumnIndexOrThrow(CHAVE_ATUALIZA_INICIO))) {
				cnf.putBoolean(CHAVE_ATUALIZA_INICIO, tmp.getInt(tmp
						.getColumnIndexOrThrow(CHAVE_ATUALIZA_INICIO)) == 1);
			} else {
				cnf.putBoolean(CHAVE_ATUALIZA_INICIO, true);
			}
			if (!tmp.isNull(tmp.getColumnIndexOrThrow(CHAVE_CAPA))) {
				cnf.putInt(CHAVE_CAPA,
						tmp.getInt(tmp.getColumnIndexOrThrow(CHAVE_CAPA)));
			} else {
				cnf.putInt(CHAVE_CAPA, 4);
			}
			tmp.close();
			return cnf;
		}
		if (!tmp.isClosed())
			tmp.close();
		return null;
	}

	public TarefasDbAdapter open() throws SQLException {

		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		setAberto(true);
		cnf = getConfiguracoesAntigas(mDb);

		return this;
	}

	public void close() {
		setAberto(false);
		mDbHelper.close();
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public long adicionaTarefa(Long id, String name, Long idSeries,
			String lista, String prio, String limite, String hora,
			String repeticao, String url) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_IDSERIES, idSeries);
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_NAME, name);
		initialValues.put(CHAVE_LISTA, lista);
		initialValues.put(CHAVE_PRIO, prio);
		initialValues.put(CHAVE_DATAPREVISTA, limite);
		initialValues.put(CHAVE_HORAPREVISTA, hora);
		initialValues.put(CHAVE_REPETICAO, repeticao);
		initialValues.put(CHAVE_URL, url);
		return mDb.insert(DATABASE_TB_TAREFAS, null, initialValues);
	}

	public long adicionaTags(Long id, String name, Long idSeries) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_IDSERIES, idSeries);
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_TAGS, name);

		return mDb.insert(DATABASE_TB_TAGS, null, initialValues);
	}

	public long adicionaNotas(Long id, Long idNota, String titulo, String nota) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_IDNOTA, idNota);
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_NOTA, nota);
		initialValues.put(CHAVE_TITULO, titulo);

		return mDb.insert(DATABASE_TB_NOTAS, null, initialValues);
	}

	public long adicionaTarefaTemporaria(Long id, String name, Long idSeries,
			String lista, String prio, String limite, String hora,
			String repeticao, String url) {
		Long resultado;
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_IDSERIES, idSeries);
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_NAME, name);
		initialValues.put(CHAVE_LISTA, lista);
		initialValues.put(CHAVE_PRIO, prio);
		initialValues.put(CHAVE_DATAPREVISTA, limite);
		initialValues.put(CHAVE_HORAPREVISTA, hora);
		initialValues.put(CHAVE_REPETICAO, repeticao);
		initialValues.put(CHAVE_URL, url);
		Cursor temp = getTarefaTemporariaPelosIds(id, idSeries);
		if (temp == null || temp.isClosed() || temp.getCount() == 0)
			resultado = mDb.insert(DATABASE_TB_TAREFAS_TEMP, null,
					initialValues);
		else
			resultado = Long.valueOf(mDb.update(DATABASE_TB_TAREFAS_TEMP,
					initialValues, CHAVE_IDSERIES + "=" + idSeries + " AND "
							+ CHAVE_ID + "=" + id, null));
		temp.close();
		return resultado;
	}

	public long adicionaTagsTemporarias(Long id, String name, Long idSeries) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_IDSERIES, idSeries);
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_TAGS, name);

		return mDb.insert(DATABASE_TB_TAGS_TEMP, null, initialValues);
	}

	public long adicionaOperacao(Long id, int tipo, Long idSeries, String lista) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_IDSERIES, idSeries);
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_TIPO, tipo);
		initialValues.put(CHAVE_NOME_LISTA, lista);

		return mDb.insert(DATABASE_TB_OPERACOES, null, initialValues);
	}

	public long insertAuthToken(String auth_token) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_AUTH, auth_token);
		return mDb.insert(DATABASE_TB_CONFIGURACOES, null, initialValues);
	}

	public long insereConfiguracoes(String auth_token, boolean atualizaInicio) {

		ContentValues initialValues = new ContentValues();

		initialValues.put(CHAVE_AUTH, auth_token);
		initialValues.put(CHAVE_ATUALIZA_INICIO, atualizaInicio);

		return mDb.insert(DATABASE_TB_CONFIGURACOES, null, initialValues);
	}

	public long adicionaListas(Long id, String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CHAVE_ID, id);
		initialValues.put(CHAVE_NOME_LISTA, name);

		return mDb.insert(DATABASE_TB_LISTA, null, initialValues);
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deletaTarefa(Long Id, Long idSeries) {

		return mDb.delete(DATABASE_TB_TAREFAS, CHAVE_ID + "=" + Id + " AND "
				+ CHAVE_IDSERIES + "=" + idSeries, null) > 0;
	}

	public boolean deletaOperacao(Long Id, Long idSeries, int tipo) {

		return mDb.delete(DATABASE_TB_OPERACOES, CHAVE_ID + "=" + Id + " AND "
				+ CHAVE_IDSERIES + "=" + idSeries + " AND " + CHAVE_TIPO + "="
				+ tipo, null) > 0;
	}

	public boolean deletaTagsPorTarefa(Long Id, Long idSeries) {

		return mDb.delete(DATABASE_TB_TAGS, CHAVE_ID + "=" + Id + " AND "
				+ CHAVE_IDSERIES + "=" + idSeries, null) > 0;
	}

	public boolean deletaTagsPorTarefaTemporaria(Long Id, Long idSeries) {

		return mDb.delete(DATABASE_TB_TAGS_TEMP, CHAVE_ID + "=" + Id + " AND "
				+ CHAVE_IDSERIES + "=" + idSeries, null) > 0;
	}

	public boolean deletaTodasTarefas() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_TAREFAS, null, null);

		return a > 0;
	}

	public boolean deletaTodasTarefasTemporarias() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_TAREFAS_TEMP, null, null);

		return a > 0;
	}

	public boolean deletaTodasOperacoes() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_OPERACOES, null, null);

		return a > 0;
	}

	public boolean deletaTodasTags() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_TAGS, null, null);

		return a > 0;
	}

	public boolean deletaTodasTagsTemporarias() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_TAGS_TEMP, null, null);

		return a > 0;
	}

	public boolean deletaTodasNotas() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_NOTAS, null, null);

		return a > 0;
	}

	public boolean deletaTodasListas() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_LISTA, null, null);

		return a > 0;
	}

	public boolean deletaConfiguracoes() {

		int a = 0;
		a = mDb.delete(DATABASE_TB_CONFIGURACOES, null, null);

		return a > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor getTodasTarefas(String ordem) {

		return mDb.query(DATABASE_TB_TAREFAS, new String[] { CHAVE_ID,
				CHAVE_IDSERIES, CHAVE_NAME, CHAVE_LISTA, CHAVE_PRIO,
				CHAVE_DATAPREVISTA, CHAVE_HORAPREVISTA, CHAVE_REPETICAO,
				CHAVE_URL }, null, null, null, null, CHAVE_PRIO + ","
				+ CHAVE_DATAPREVISTA + "," + CHAVE_HORAPREVISTA);
	}

	public Cursor getTarefasPorLista(String ordem, String lista) {

		return mDb.query(DATABASE_TB_TAREFAS, new String[] { CHAVE_ID,
				CHAVE_IDSERIES, CHAVE_NAME, CHAVE_LISTA, CHAVE_PRIO,
				CHAVE_DATAPREVISTA, CHAVE_HORAPREVISTA, CHAVE_REPETICAO,
				CHAVE_URL }, CHAVE_LISTA + "=" + lista, null, null, null,
				CHAVE_PRIO + "," + CHAVE_DATAPREVISTA + ","
						+ CHAVE_HORAPREVISTA);
	}

	public Cursor getTarefasPorTag(String tag) {

		return mDb.query(DATABASE_TB_TAGS, new String[] { CHAVE_ID,
				CHAVE_IDSERIES }, CHAVE_TAGS + "='" + tag + "'", null, null,
				null, null);
	}

	public ArrayList<Tarefa> retornaTarefasDeUmaTag(String tag) {
		ArrayList<Tarefa> tarefas = new ArrayList<Tarefa>();

		Cursor tmpTar = getTarefasPorTag(tag);

		preencheAsTagsDasTarefas(tarefas, tmpTar);

		return tarefas;
	}

	private ArrayList<Tarefa> preencheAsTagsDasTarefas(
			ArrayList<Tarefa> tarefas, Cursor tmpTar) {
		Cursor tmpT = null;
		Tarefa t = null;
		Long id, idSeries;

		if (tmpTar == null || tmpTar.isClosed() || tmpTar.getCount() <= 0)
			return null;
		Cursor tmp = null;

		for (int i = 0; i < tmpTar.getCount(); i++) {
			tmpTar.moveToPosition(i);
			id = tmpTar.getLong(tmpTar.getColumnIndexOrThrow(CHAVE_ID));
			idSeries = tmpTar.getLong(tmpTar
					.getColumnIndexOrThrow(CHAVE_IDSERIES));
			tmpT = getTarefaPelosIds(id, idSeries);
			if (tmpT != null && tmpT.moveToFirst()) {
				t = new Tarefa();

				t.setName(tmpT.getString(tmpT.getColumnIndexOrThrow(CHAVE_NAME)));
				t.setId(id);
				t.setIdSeries(idSeries);
				t.setLista(tmpT.getString(tmpT
						.getColumnIndexOrThrow(CHAVE_LISTA)));
				t.setPrioridade(tmpT.getString(tmpT
						.getColumnIndexOrThrow(CHAVE_PRIO)));
				t.setRepeticao(tmpT.getString(tmpT
						.getColumnIndexOrThrow(CHAVE_REPETICAO)));
				t.setDataPrevista(TrataData.trata(tmpT.getString(tmpT
						.getColumnIndexOrThrow(CHAVE_DATAPREVISTA))));
				t.setHoraPrevista(tmpT.getString(tmpT
						.getColumnIndexOrThrow(CHAVE_HORAPREVISTA)));
				tmp = getTagsPorTarefa(t.getId(), t.getIdSeries());

				t.setTags(TarefasDbAdapter.deCursorParaArrayListTags(tmp));
				t.setUrl(tmpT.getString(tmpT.getColumnIndexOrThrow(CHAVE_URL)));

				if (tmp != null)
					tmp.close();
				tarefas.add(t);
			}
		}

		tmpT.close();
		tmpTar.close();

		return tarefas;
	}

	public Cursor getTarefaPelosIds(Long id, Long idSeries) throws SQLException {

		Cursor mCursor =

		mDb.query(
				true,
				DATABASE_TB_TAREFAS,
				new String[] { CHAVE_IDSERIES, CHAVE_ID, CHAVE_NAME,
						CHAVE_LISTA, CHAVE_DATAPREVISTA, CHAVE_HORAPREVISTA,
						CHAVE_PRIO, CHAVE_REPETICAO, CHAVE_URL },
				CHAVE_IDSERIES + "=" + idSeries + " AND " + CHAVE_ID + "=" + id,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor getTarefaTemporariaPelosIds(Long id, Long idSeries)
			throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TB_TAREFAS_TEMP, new String[] {
				CHAVE_IDSERIES, CHAVE_ID, CHAVE_NAME, CHAVE_LISTA,
				CHAVE_DATAPREVISTA, CHAVE_HORAPREVISTA, CHAVE_PRIO,
				CHAVE_REPETICAO, CHAVE_URL }, CHAVE_IDSERIES + "=" + idSeries
				+ " AND " + CHAVE_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor getTagsPorTarefa(Long id, Long idSeries) throws SQLException {

		return mDb.query(false, DATABASE_TB_TAGS, new String[] { CHAVE_TAGS,
				CHAVE_ID, CHAVE_IDSERIES }, CHAVE_IDSERIES + "=" + idSeries
				+ " AND " + CHAVE_ID + "=" + id, null, null, null, null, null);

	}

	public Cursor getNotasPorTarefa(Long id) throws SQLException {

		return mDb.query(DATABASE_TB_NOTAS, new String[] { CHAVE_ID,
				CHAVE_TITULO, CHAVE_NOTA }, CHAVE_ID + "=" + id, null, null,
				null, null, null);

	}

	public Cursor getTagsPorTarefaTemporaria(Long id, Long idSeries)
			throws SQLException {

		return mDb.query(false, DATABASE_TB_TAGS_TEMP, new String[] {
				CHAVE_TAGS, CHAVE_ID, CHAVE_IDSERIES }, CHAVE_IDSERIES + "="
				+ idSeries + " AND " + CHAVE_ID + "=" + id, null, null, null,
				null, null);

	}

	public String getAuthToken() throws SQLException {
		Cursor tmp = mDb
				.query(false, DATABASE_TB_CONFIGURACOES,
						new String[] { CHAVE_AUTH }, null, null, null, null,
						null, null);
		if (!tmp.isClosed() && tmp.getCount() > 0) {
			tmp.moveToFirst();
			String auth = tmp.getString(tmp.getColumnIndexOrThrow(CHAVE_AUTH));
			tmp.close();
			return auth;
		}
		if (!tmp.isClosed())
			tmp.close();
		return "";

	}

	public boolean atualizarTarefa(Long idSeries, Long id, String name,
			String dataPrevista, String lista, String prioridade,
			String horaPrevista, String repeticao, String url) {
		ContentValues args = new ContentValues();
		args.put(CHAVE_NAME, name);
		args.put(CHAVE_DATAPREVISTA, dataPrevista);
		args.put(CHAVE_HORAPREVISTA, horaPrevista);
		args.put(CHAVE_LISTA, lista);
		args.put(CHAVE_PRIO, prioridade);
		args.put(CHAVE_REPETICAO, repeticao);
		args.put(CHAVE_URL, url);
		return mDb.update(DATABASE_TB_TAREFAS, args, CHAVE_IDSERIES + "="
				+ idSeries + " AND " + CHAVE_ID + "=" + id, null) > 0;
	}

	public boolean atualizarSincronizacao(boolean sinc) {
		ContentValues args = new ContentValues();

		args.put(CHAVE_ATUALIZA_INICIO, sinc);

		return mDb.update(DATABASE_TB_CONFIGURACOES, args, null, null) > 0;
	}

	public boolean atualizarCapa(int capa) {
		ContentValues args = new ContentValues();

		args.put(CHAVE_CAPA, capa);

		return mDb.update(DATABASE_TB_CONFIGURACOES, args, null, null) > 0;
	}

	public static ArrayList<String> deCursorParaArrayListTags(Cursor mCursor) {
		ArrayList<String> tags = new ArrayList<String>();

		if (mCursor == null)
			return null;
		for (int i = 0; i < mCursor.getCount(); i++) {
			mCursor.moveToPosition(i);
			// The Cursor is now set to the right position
			tags.add(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_TAGS)));

		}

		return tags;
	}

	public static ArrayList<Lista> deCursorParaArrayListListas(Cursor mCursor) {
		ArrayList<Lista> listas = new ArrayList<Lista>();
		Lista l = null;
		if (mCursor == null)
			return null;
		for (int i = 0; i < mCursor.getCount(); i++) {
			mCursor.moveToPosition(i);
			l = new Lista(Long.parseLong(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_ID))),
					mCursor.getString(mCursor
							.getColumnIndexOrThrow(CHAVE_NOME_LISTA)));
			// The Cursor is now set to the right position
			listas.add(l);

			l = null;

		}

		return listas;
	}

	public static ArrayList<Nota> deCursorParaArrayListNotas(Cursor mCursor) {
		ArrayList<Nota> notas = new ArrayList<Nota>();
		Nota n = null;
		if (mCursor == null)
			return null;
		for (int i = 0; i < mCursor.getCount(); i++) {
			mCursor.moveToPosition(i);
			n = new Nota();
			n.setId(Long.parseLong(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_ID))));
			n.setTitulo(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_TITULO)));
			n.setNota(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_NOTA)));

			notas.add(n);

			n = null;

		}

		return notas;
	}

	// APAGAR
	public static ArrayList<Operacao> deCursorParaArrayListOperacoes(
			Cursor mCursor) {
		ArrayList<Operacao> tags = new ArrayList<Operacao>();
		Operacao op = null;

		if (mCursor == null)
			return null;
		for (int i = 0; i < mCursor.getCount(); i++) {
			mCursor.moveToPosition(i);
			op = new Operacao();
			// The Cursor is now set to the right position
			op.setTipo(mCursor.getInt(mCursor.getColumnIndexOrThrow(CHAVE_TIPO)));
			op.setIdSeriesTarefa(mCursor.getLong(mCursor
					.getColumnIndexOrThrow(CHAVE_IDSERIES)));
			op.setIdTarefa(mCursor.getLong(mCursor
					.getColumnIndexOrThrow(CHAVE_ID)));
			op.setListaTarefa(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_NOME_LISTA)));

			tags.add(op);

		}

		return tags;
	}

	public static ArrayList<Tarefa> deCursorParaArrayListTarefas(Cursor mCursor) {
		ArrayList<Tarefa> tarefas = new ArrayList<Tarefa>();
		Tarefa tmp = null;
		if (mCursor == null)
			return null;
		for (int i = 0; i < mCursor.getCount(); i++) {
			mCursor.moveToPosition(i);
			tmp = new Tarefa();
			// The Cursor is now set to the right position

			tmp.setName(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_NAME)));
			tmp.setId(mCursor.getLong(mCursor.getColumnIndexOrThrow(CHAVE_ID)));
			tmp.setIdSeries(mCursor.getLong(mCursor
					.getColumnIndexOrThrow(CHAVE_IDSERIES)));
			tmp.setLista(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_LISTA)));
			tmp.setPrioridade(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_PRIO)));
			tmp.setDataPrevista(TrataData.trata(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_DATAPREVISTA))));
			tmp.setHoraPrevista(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_HORAPREVISTA)));
			tmp.setRepeticao(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_REPETICAO)));
			tmp.setUrl(mCursor.getString(mCursor
					.getColumnIndexOrThrow(CHAVE_URL)));
			tarefas.add(tmp);
			tmp = null;
		}

		return tarefas;
	}

	public Cursor getTarefasNoIntervalo(String inferior, String superior,
			String ordem) {
		Cursor resultado;

		resultado = mDb.query(DATABASE_TB_TAREFAS, new String[] { CHAVE_ID,
				CHAVE_IDSERIES, CHAVE_NAME, CHAVE_LISTA, CHAVE_PRIO,
				CHAVE_DATAPREVISTA, CHAVE_HORAPREVISTA, CHAVE_REPETICAO,
				CHAVE_URL }, CHAVE_DATAPREVISTA + " BETWEEN '" + inferior
				+ "' AND '" + superior + "'", null, null, null, ordem);

		return resultado;
	}

	public ArrayList<Tarefa> retornaTarefasNoIntervalo(String inferior,
			String superior, String ordem) {
		ArrayList<Tarefa> tarefas = new ArrayList<Tarefa>();
		Cursor tmpTar = getTarefasNoIntervalo(inferior, superior, ordem);

		preencheAsTagsDasTarefas(tarefas, tmpTar);

		return tarefas;
	}

	public Cursor getTodasTags() {
		return mDb.query(true, DATABASE_TB_TAGS, new String[] { CHAVE_TAGS },
				null, null, null, null, null, null);
	}

	public Cursor getTodasListas() {
		return mDb.query(false, DATABASE_TB_LISTA, new String[] {
				CHAVE_NOME_LISTA, CHAVE_ID }, null, null, null, null, null,
				null);
	}

	public Cursor getListasExceto(String listaFora) {
		return mDb.query(false, DATABASE_TB_LISTA, new String[] {
				CHAVE_NOME_LISTA, CHAVE_ID }, CHAVE_NOME_LISTA + "<> '"
				+ listaFora + "'", null, null, null, null, null);
	}

	public Cursor getTodasOperacoes() {
		return mDb.query(false, DATABASE_TB_OPERACOES, new String[] { CHAVE_ID,
				CHAVE_IDSERIES, CHAVE_TIPO, CHAVE_NOME_LISTA }, null, null,
				null, null, null, null);
	}
}
