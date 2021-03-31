package com.vconsulte.sij.clipping;

/*
 * 	versao 2.4.08	- 28 de Novembro 2020
					- Camada do método atualizaEdicoes após loop de edicoes
					- Ajustes nas linhas de log
					- 
					- 
					- 
*/

	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	//import java.io.PrintWriter;
	import java.io.UnsupportedEncodingException;
	//import java.sql.ResultSet;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Date;
	//import java.util.GregorianCalendar;
	//import java.util.Iterator;
	import java.util.List;
	//import java.util.Map;
	//import java.util.Set;
	//import java.util.Date;
	import java.text.DateFormat;
	//import java.text.SimpleDateFormat;
	
	import org.apache.chemistry.opencmis.client.api.Folder;
	import org.apache.chemistry.opencmis.client.api.Session;
	import com.vconsulte.sij.base.InterfaceServidor;
	import com.vconsulte.sij.base.GravaTexto;
	import com.vconsulte.sij.base.Comuns;

public class Clippingb {

	static InterfaceServidor conexao = new InterfaceServidor();
	static Session sessao;
	static File config;
	
	static List <String> idEdicoes = new ArrayList<String>();
	static List <String> log = new ArrayList<String>();
	
	static String [] parametros = null;
	
	static String cliente;
	static String usuario;
	static String url;
	static String password;
	static String pastaCarregamento;
	static String pastaPublicacoes;
	static String pastaTokens;
	static String pastaSaida;
	static String pastaLog;
	static String tipoDocumento;
	static String edtFolderName = "";
	static String linhasIndexadas = "";

	static String idToken;
	
	static String[] listaEdicoes = new String[55];
	static String[] listData = new String[55];
	static String publicacoesLocalizadas[][] = new String [5000][2];

	static String edicaoEscolhida = "";	
	static String token = "";
    static String a = null;
    static String newline = "\n";
    static String caminho = "";
    static String tribunal = "todos";

	static int k =0;
	static int opcao;
	static int limiteClientes;
	
	static boolean escolheu = false;
	static boolean parametrizado = false;
	final static String tipoProcessamento = "BATCH";

	public static void main(String[] args) throws Exception {
		processamento();
	}
	
	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

    private static void processamento() throws Exception, IOException, Exception {

    	registraLog("Início do processamento do Clippingb\n");
    	registraLog("Carregando tabelas");
    	com.vconsulte.sij.base.Parametros.carregaTabelas();
    	registraLog("Carregando configuração");
    	com.vconsulte.sij.base.Configuracao.carregaConfig();

    	cliente = com.vconsulte.sij.base.Parametros.CLIENTE;
		url = com.vconsulte.sij.base.Parametros.URL;
		usuario = com.vconsulte.sij.base.Parametros.USUARIO;
		password = com.vconsulte.sij.base.Parametros.PASSWORD;
		limiteClientes = com.vconsulte.sij.base.Parametros.LIMITECLIENTES;
		pastaCarregamento = com.vconsulte.sij.base.Parametros.PASTACARREGAMENTO;
		pastaPublicacoes = com.vconsulte.sij.base.Parametros.PASTAPUBLICACOES;
		pastaSaida = com.vconsulte.sij.base.Parametros.PASTASAIDA;
		pastaTokens = com.vconsulte.sij.base.Parametros.PASTATOKENS;
		tipoDocumento = com.vconsulte.sij.base.Parametros.TIPODOCUMENTO;
		pastaLog = com.vconsulte.sij.base.Parametros.LOGFOLDER;
		String [][] tabClientes = new String[limiteClientes][2];
		
		System.out.print("\n");
		Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Início do processamento do Clipping.",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	System.out.print("\n");
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Parâmetros de processamento:",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tServidor: " + url,tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Carregamnto: " + pastaCarregamento,tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Publicações: " +pastaPublicacoes,tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Saida: " +pastaSaida,tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Tokesns: " +pastaTokens,tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta de Logs: " +pastaLog,tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	System.out.print("\n");
    	registraLog("Conectando servidor.");
    	Comuns.apresentaMenssagem("Conectando com o servidor.",tipoProcessamento,"informativa", null);
		sessao = Comuns.conectaServidor(usuario, password, url);
        Comuns.apresentaMenssagem("Servidor conectado com sucesso.",tipoProcessamento,"informativa", null);

		if (sessao == null) {
			registraLog("Falha na conexção com o servidor, processamento encerrado.");
    		Comuns.apresentaMenssagem("Falha na conexão com o servidor",tipoProcessamento,"erro", null);
			Comuns.finalizaProcesso(tipoProcessamento);
		}
		registraLog("Carregando tabela de clientes");
		tabClientes = conexao.listarClientes(sessao, limiteClientes);	
		for(int x=0; x<=limiteClientes; x++) {
			if(tabClientes[x][0] == null) {
				registraLog("Fim do carregamento da tabela de clientes.");
				break;
			}
			indexador(tabClientes);			
		}
		String arquivoLog = "logClipping"+obtemHrAtual() + ".log";
		registraLog("Gravando log.");
		registraLog("Fim do Processamento.");
		gravaLogClipagem(log, arquivoLog);
		System.out.println("\n");
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Fim do processamento do Clipping.",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
		
    	//Comuns.finalizaProcesso(tipoProcessamento);
    }
  
    private static void indexador(String [][] tabClientes) throws NullPointerException, IOException, Exception {
    	int ix = 0;
    	int publicacoesIncluidas = 0;
    	int localizados = 0;
    	int naoLocalizados = 0;
    	int totalPublicacoes = 0;
    	int totalTokens = 0;
    	boolean edicaoRegistrada = false;
    	String linha = "";
    	String cliente = "";
    	String destino = "";   	
    	String tokenTribunal = "";
    	String [][] edicoesIndexadas = new String [1000][2];
    	String dummy = "";
    	List <String> tabelasTokens = new ArrayList<String>();
    	List <String> tokens = new ArrayList<String>();   	
    	List <String> publicacoes = new ArrayList<String>();
    	List <String> edicoesSelecionadas = new ArrayList<String>();
    	registraLog("Início da Indexação.");
       
// loop de clientes
    	for(int ix1=0; ix1<=limiteClientes; ix1++) {														// ix1 - obtem lista de clientes
        	if(tabClientes[ix1][0] == null) {
        		break;
        	}
        	int dupl = 0;
        	cliente = tabClientes[ix1][0];
        	destino = tabClientes[ix1][1]; 
        	registraLog("Obtendo tabelas de tokens para o cliente: " + tabClientes[ix1][0]);
        	tabelasTokens = conexao.obtemTabelasTokens(sessao, cliente);
        	if(!tabelasTokens.isEmpty()) {
        	
// Loop de tabelas tokens        
	        	for(int ix2=0; ix2<=tabelasTokens.size()-1; ix2++) {					// ix2 - obtem tabTokens do cliente
	        		
	        		if(!linhasIndexadas.isEmpty()) {
	        			dummy = getDateTime().replace("/", "-");
	        			dummy = dummy.replace(":", "-");
	        			dummy = dummy.replace(" ", "_");
	        			linhasIndexadas = linhasIndexadas + "\n-----------------------------------------------------------------\n";
	        			registraLog("Gravando relatorio de recorte: " + pastaLog + "/" + tokenTribunal + "-indexacao-" + dummy + ".txt");
	        			gravaArquivo(pastaLog + "/" + tokenTribunal + "-indexacao-" + dummy + ".txt",linhasIndexadas);
	        		} 
	        		
	        		linhasIndexadas = "";
	        		linhasIndexadas = "Processamento da localização de publicações: " + getDateTime();
	        		
// carrega tabela de tokens
	        		tokenTribunal = InterfaceServidor.obtemTribunalToken(sessao, tabelasTokens.get(ix2));
	        		registraLog("Início da localização das publicações para o tribunal: " + tokenTribunal);
	        		Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
	        		Comuns.apresentaMenssagem("Início da localização das publicações para o tribunal: " + tokenTribunal, tipoProcessamento,"informativa", null);
	        		linhasIndexadas = getDateTime() + ">>> Início do processamento do tribunal: " + cliente + "/" + tokenTribunal + " <<<\n";
	        		linhasIndexadas = linhasIndexadas + "-----------------------------------------------------------------\n";
	        		registraLog(">>> Início do processamento do tribunal: " + cliente + "/" + tokenTribunal + " <<<");
	        		
	        		localizados = 0;
	        		naoLocalizados = 0;
	        		publicacoesIncluidas = 0;
	        		totalTokens = 0;
	        		edicoesSelecionadas.clear();
	        		registraLog("Selecionando edições para o tribunal: " + cliente + "/" + tokenTribunal);
	        		
// seleciona edicoes
	        		edicoesSelecionadas = conexao.listarEdicoesPorTribunal(sessao, tokenTribunal);
	        		registraLog(edicoesSelecionadas.size() + " edição selecionada para o tribunal: " + cliente + "/" + tokenTribunal);
	        		registraLog("-----------------------------------------------------------------\"");
	        		
	        		if(edicoesSelecionadas.isEmpty()) {
	        			registraLog("Não há edicoes para este tribunal: " + cliente + "/" + tokenTribunal);
	        			Comuns.apresentaMenssagem("Não há edições para este tribunal: " + cliente + "/" + tokenTribunal,tipoProcessamento,"informativa", null);
	        			Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
	        			System.out.println("\n");
	        			linhasIndexadas = linhasIndexadas + " Não há edicoes para este tribunal: "  + cliente + " - " + tokenTribunal; 
	        			continue;
	        		} else {               		
	            		registraLog("Localizada(s) " + edicoesSelecionadas.size() + " edições não indexada(s) para o tribunal " + cliente + "/" + tokenTribunal);
	            		Comuns.apresentaMenssagem("Localizada(s) edições " + edicoesSelecionadas.size() + " não indexada(s) para o tribunal " + cliente + "/" + tokenTribunal, tipoProcessamento,"informativa", null);
k++;	        		
	        		}
	  
	        		registraLog("Início do loop de tokens para o tribunal: " + cliente + "/" + tokenTribunal);
	        		Comuns.apresentaMenssagem("Início do loop de tokens para o tribunal: " + cliente + "/" + tokenTribunal, tipoProcessamento,"informativa", null);
	        		linhasIndexadas = linhasIndexadas + "\n" + Comuns.obtemHrAtual() + " - Localizando publicações para o tribunal: " + cliente + "/" + tokenTribunal + " -> " + edicoesSelecionadas.size()+"\n";     

// pesquisa nas edicoes selecionadas
	        		for(int ix3=0; ix3<=edicoesSelecionadas.size()-1;ix3++) {			// ix3 - ediçoes selc para o tribunal da tabToken
	
	        			registraLog("Verificando se a edição: " + edicoesSelecionadas.get(ix3) + " já foi verificada para o tribunal " + cliente + "/" + tokenTribunal);
	        			Comuns.apresentaMenssagem("Verificando se a edição:: " + cliente + "/" + tokenTribunal  + " já foi verificada para o tribunal ", tipoProcessamento,"informativa", null);
	        			
// verifica se a edição já foi clipada
	        			if(conexao.verificaEdicaoNaoClipada(sessao, edicoesSelecionadas.get(ix3), cliente)) {
	        				registraLog("A edição: " + edicoesSelecionadas.get(ix3) + "  ainda não foi verificada para o tribunal " + cliente + "/" + tokenTribunal);
	        				Comuns.apresentaMenssagem("A edição: " + cliente + "/" + tokenTribunal  + " ainda não foi verificada.", tipoProcessamento,"informativa", null);
	        				edicoesIndexadas[ix][0] = cliente;
				        	edicoesIndexadas[ix][1] = edicoesSelecionadas.get(ix3);
	        				ix++;
	        				registraLog("Carregando tabela de tokens para o tribunal: " + cliente + "/" + tokenTribunal);
				        	tokens = conexao.carregaTokensBatch(sessao, tabelasTokens.get(ix2));
				        	
				        	if(tokens.isEmpty()) {
				        		registraLog("Nã há tokens na tabela de tokens para o tribunal: " + cliente + "/" + tokenTribunal);
				        		linhasIndexadas = linhasIndexadas + "\n" + Comuns.obtemHrAtual() + " Nã há tokens na tabela de tokens para o tribunal:  " + cliente + "/" + tokenTribunal + " -> " + edicoesSelecionadas.size()+"\n"; 
				        		continue;
				        	} else {
					        	totalTokens = tokens.size();
					        	registraLog("Tokens carregados");
					        	registraLog("Carregados " + totalTokens + " tokens desta tabela de tokens do tribunal: " + cliente + "/" + tokenTribunal);
		        				edicaoRegistrada = false;
		        				
// procura por tokens
		        				for(int ix4=0; ix4<=tokens.size()-1; ix4++) {				// ix4 - procura por cada token
		        					publicacoes = conexao.localizaPublicacoes(sessao, edicoesSelecionadas.get(ix3), tokenTribunal, tokens.get(ix4).trim());
		        					if(!publicacoes.isEmpty()) {
		        						registraLog("Localizados: " + publicacoes.size() + " publicações para o token: " + tokens.get(ix4).trim());
		        						linhasIndexadas = linhasIndexadas + "\n  Localizado(s):  " + publicacoes.size() + " publicações para o token: " + tokens.get(ix4).trim() + "\n\n"; 
		        						Comuns.apresentaMenssagem("Localizados: " + publicacoes.size() + " publicações para o token: " + tokens.get(ix4).trim(), tipoProcessamento,"informativa", null);
						        		localizados++;
// registra publicações localizadas
						        		for(int ix5=0; ix5<=publicacoes.size()-1; ix5++) {	// registra publicacoes localizadas
				        					dupl = verificaDuplicidadeEditais(publicacoes.get(ix5));		        					
				        					if(dupl >= 0) {
				        						publicacoesLocalizadas[dupl][1] = publicacoesLocalizadas[dupl][1] + "\n" + tokens.get(ix4).trim();
				        						registraLog("*** Publicação atualizada: " + publicacoesLocalizadas[publicacoesIncluidas][0] + " - " + publicacoesLocalizadas[publicacoesIncluidas][1]+"\n");
k++;				        						
				        					} else {		        					
				        						publicacoesLocalizadas[publicacoesIncluidas][0] = publicacoes.get(ix5).trim();
				            					publicacoesLocalizadas[publicacoesIncluidas][1] = tokens.get(ix4).trim();		            					            					
				            					registraLog("+++ Publicação incluída: " + publicacoesLocalizadas[publicacoesIncluidas][0] + " - " + publicacoesLocalizadas[publicacoesIncluidas][1]+"\n"); 					
				            					publicacoesIncluidas++;
k++;
				        					}		        					
					        			}
// fim do registro das publicações localizadas 
k++;						        		
				        			} else {
				        				linhasIndexadas = linhasIndexadas + "Token não localizado: " + tokens.get(ix4).trim() + "\n";
				        				registraLog("Token não localizado: " + tokens.get(ix4).trim());
				        				Comuns.apresentaMenssagem("Token não localizado: " + tokens.get(ix4).trim(), tipoProcessamento,"informativa", null);
				        				naoLocalizados++;
				        			}
k++;
						        }												// fim do loop de publicacoes selecionadas
// Fim da procura por tokens

k++;
		        				
		                		if(localizados == 0) {
		                			linha = ">>> " + cliente + "/" + tokenTribunal + "\t\t *** Nenhuma publicação selecionada ***";
		                			registraLog("\">>> \" + cliente + \"/\" + tokenTribunal + \"\\t\\t *** Nenhuma publicação selecionada ***\"");
		                			linhasIndexadas = linhasIndexadas + "\n" + linha + "\n";   
		                			Comuns.apresentaMenssagem(linha, tipoProcessamento,"informativa", null);
		                		} else {
		                			totalPublicacoes = totalPublicacoes + publicacoesIncluidas;
		                			linha = ">>> " + cliente + "-" + tokenTribunal + " - Total de tokens: " + totalTokens + " / Localizados: \t" + localizados + " / Não localizados: " + naoLocalizados + " / Publicações afetadas: " + publicacoesIncluidas;
			        				registraLog("\">>> \" + cliente + \"-\" + tokenTribunal + \" - Total de tokens: \" + totalTokens + \" / Localizados: \\t\" + localizados + \" / Não localizados: \" + naoLocalizados + \" / Publicações afetadas: \" + publicacoesIncluidas");
			        				linhasIndexadas = linhasIndexadas + linha + "\n";      		   		
			                		Comuns.apresentaMenssagem(linha, tipoProcessamento,"informativa", null);
		                			registraLog("Copiando publicações localizadas para o site do cliente.");
		                			copiaPublicacoes(cliente, destino, tokenTribunal);
		                			dummy = getDateTime().replace(":", "-");
		                			dummy = getDateTime().replace("/", "-");
		                			linhasIndexadas = linhasIndexadas + "\n-----------------------------------------------------------------\n";
		                			registraLog("Gravando relatorio de indexação para o tribunal: " + cliente + "/" + tokenTribunal);
		                			
		                			dummy = getDateTime().replace("/", "-");
		    	        			dummy = dummy.replace(":", "-");
		    	        			dummy = dummy.replace(" ", "_");		                			
		                			gravaArquivo(pastaLog + "/" + tokenTribunal + "-indexacao-" + dummy + ".txt",linhasIndexadas);
		                			linhasIndexadas = "";
		                			limparPublicacoesLocalizadas();
		                		}
		                		
	        				}

	        			} else {
	        				k++;
	        			}
	        			
	        		//	registraLog("Edições não indexadas para o tribunal " + cliente + "/" + tokenTribunal + "\n");
	            	//	Comuns.apresentaMenssagem("Localizando não indexadas para o tribunal " + cliente + "/" + tokenTribunal, tipoProcessamento,"informativa", null);

// Fim do loop de ediçoes selecionadas		
	        		}														// fim do loop de edicoes selecionadas
	        		//atualizaEdicoes(edicoesIndexadas);
	        		//System.out.println("\n");
	        		//localizados = 0;
	        		//naoLocalizados = 0;
k++;
	        	}															// fim do loop de tabelas tokens
        	} else {
        		registraLog("Não existem tabelas tokens para processamento." + totalPublicacoes);
        		Comuns.apresentaMenssagem("Não existem tabelas tokens para processamento.", tipoProcessamento, "informativa", null);
        		linhasIndexadas = linhasIndexadas + "Não existem tabelas tokens para processamento." ;
k++;
        	}
        	linhasIndexadas = linhasIndexadas + "Total de publicações selecionadas: " + totalPublicacoes;
        	registraLog("Total de publicações selecionadas: " + totalPublicacoes);
        	Comuns.apresentaMenssagem("Total de publicações selecionadas: " + publicacoesIncluidas + "\n", tipoProcessamento, "informativa", null);
        	Comuns.gravaLog(pastaLog, Comuns.obtemHrAtual().replace(":", ""), "ClipB",log);
k++;
        }																	// fim do loop de clientes
    	registraLog("Atualizando edições clipadas.");
 
 // ATENÇÃO
    	atualizaEdicoes(edicoesIndexadas); 	
    	registraLog("Fim do processamento." + totalPublicacoes);
        Comuns.apresentaMenssagem("Fim do processo de Clipping.", tipoProcessamento, "informativa", null);
k++;
    }
    
    private static void registraLog(String registroLog) {
		log.add(getDateTime() + " - " + registroLog);
	}
    
    private static String completaEsquerda(String value, char c, int size) {
		String result = value;
		while (result.length() < size) {
			result = c + result;
		}
		return result;
	}
    
    private static String obtemHrAtual() {

		String hr = "";
		String mn = "";
		String sg = "";
		Calendar data = Calendar.getInstance();
		hr = Integer.toString(data.get(Calendar.HOUR_OF_DAY));
		mn = Integer.toString(data.get(Calendar.MINUTE));
		sg = Integer.toString(data.get(Calendar.SECOND));
		return completaEsquerda(hr,'0',2)+":"+completaEsquerda(mn,'0',2)+":"+completaEsquerda(sg, '0', 2);
	}
    
    private static void gravaLogClipagem(List<String> log, String arquivoLog) throws IOException {
    	StringBuilder blocoTexto = new StringBuilder();
		String bufferSaida = "";
		blocoTexto.append(log);
		FileWriter arqSaida = new FileWriter(arquivoLog);
		BufferedWriter bw = new BufferedWriter(arqSaida);
		bufferSaida = blocoTexto.toString();
		bw.write(bufferSaida);
		bw.close();
    }
    
    private static void copiaPublicacoes(String cliente, String destino, String tribunal) throws UnsupportedEncodingException, Exception {
    	String pastaNome;
    	String descricao;
    	String strEdicao;
    	String nomePublicacao;
    	Folder idPastaDestino;
    	String idPubicacaoCopiada;
    	String queryString;
    	String linha = "";
    	Date edicao;
    	int dupl = 0;
    	int totalCopiadas = 0;
    	System.out.println("\n");
    	registraLog("Início da movimentação daNão há edicoes para este tribunals publicações localizadas");
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Início da movimentação das publicações localizadas", tipoProcessamento,"informativa", null);
		for (int ix = 0; ix <= publicacoesLocalizadas.length; ix++) {

			if(publicacoesLocalizadas[ix][0] == null) {
				break;
			}

			nomePublicacao = obtemInformacaoPublicacao(publicacoesLocalizadas[ix][0],02);
			strEdicao = obtemInformacaoPublicacao(publicacoesLocalizadas[ix][0],04);
			pastaNome = obtemInformacaoPublicacao(publicacoesLocalizadas[ix][0],12);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        sdf.setLenient(false);
	        edicao = sdf.parse(strEdicao);
	        strEdicao = strEdicao.substring(8, 10) + "-" + strEdicao.substring(5, 7) + "-" + strEdicao.substring(0, 4);
	        edtFolderName = pastaNome;
	        descricao = "Publicações localizadas para o TRT: " + tribunal + "ª Região";
			idPastaDestino = InterfaceServidor.verificaPastaPublicacao(sessao,  destino, pastaNome+"X", descricao, tribunal, edicao, cliente, strEdicao);

			if(!InterfaceServidor.copiaPublicacao(sessao, publicacoesLocalizadas[ix][0], pastaCarregamento+"/"+pastaNome, destino+"/"+pastaNome+"X", nomePublicacao, cliente)) {
			//	Comuns.apresentaMenssagem("Publicação copiada -->" + ix + "-" + publicacoesLocalizadas[ix][0], tipoProcessamento,"informativa", null);
			//	linhasIndexadas = linhasIndexadas +  "Publicação copiada -->" + ix + "-" + publicacoesLocalizadas[ix][0] + "\n";
				registraLog("Publicação copiada -->" + ix + "-" + publicacoesLocalizadas[ix][0]);
				dupl = verificaDuplicidadeEditais(publicacoesLocalizadas[ix][0]);			
			}

			queryString = "SELECT cmis:objectId FROM cmis:document WHERE in_folder('" + idPastaDestino + "') AND cmis:name='" + nomePublicacao + "' AND cmis:lastModifiedBy='sij'";
			queryString = trataQueryString(queryString);
			idPubicacaoCopiada = InterfaceServidor.getFileId(sessao, queryString);

			descricao = "Publicação localizada - TRT: " + tribunal + "ª Região - edição: "  + strEdicao + "\n\n" +
						" **** Tokens Localizados ****" + "\n" + publicacoesLocalizadas[ix][1];
			registraLog(cliente + " - Publicação incluída na site do cliente: " + cliente + " - " + nomePublicacao);
			linhasIndexadas = linhasIndexadas + " - Publicações incluída na site do cliente: " + cliente + " - " + nomePublicacao + "\n";
			Comuns.apresentaMenssagem(" - Publicação incluída no site do cliente: " + cliente + " - " + nomePublicacao, tipoProcessamento,"informativa", null);			
			InterfaceServidor.atualizaPublicacaoClipada(sessao, idPubicacaoCopiada, descricao, cliente, publicacoesLocalizadas[ix][1]);
		}
		registraLog("Fim da movimentação das publicações localizadas: " + cliente + "/" + tribunal);
		Comuns.apresentaMenssagem("Fim da movimentação das publicações localizadas para o cliente: " + cliente + "/ Tribunal: " + tribunal, tipoProcessamento,"informativa", null);
		Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    }
    
    private static String obtemInformacaoPublicacao(String idPublicacao, int indice) {
    	List <String> infoPublicacao = new ArrayList<String>();
    	infoPublicacao = InterfaceServidor.obtemInformacosPublicacao(sessao, idPublicacao);
		return infoPublicacao.get(indice);
    }
    
    private static String trataQueryString(String query) {
    	String queryTratada = "";
    	String dummy = "";
    	String [] arrayLinha = query.split(" ");
    	for(int ix=0; ix<= arrayLinha.length-1; ix++) {
    		if(arrayLinha[ix].equals("in_folder('CMIS_FOLDER") || arrayLinha[ix].equals("(cmis:folder):")) {
    			continue;
    		} else {
    			dummy = dummy + " " + arrayLinha[ix];
    		}    		
    	}
    	String [] arrayFinal = dummy.trim().split(" ");
    	for(int ix=0; ix<= arrayFinal.length-1; ix++) {
    		if(arrayFinal[ix].equals("WHERE")) {
    			queryTratada = queryTratada + " " + arrayFinal[ix] + " in_folder('" + arrayFinal[ix+1].trim();
    			ix++;
    		} else {
    			queryTratada = queryTratada + " " + arrayFinal[ix].trim();
    		}
    	}	
    	return queryTratada.trim();
    }
    
    private static int verificaDuplicidadeEditais(String idEdital) {
    	for(int ix=0; ix<=publicacoesLocalizadas.length-1; ix++) {
    		if(publicacoesLocalizadas[ix][0] != null) {
    			if(publicacoesLocalizadas[ix][0].equals(idEdital)) {
	    			return ix;
	    		}
    		} else {
    			break;
    		}	    		
    	}
    	return -1;
    }
    
    private static void atualizaEdicoes(String [][] edicoesIndexados) throws Exception {
    	int ix = 0;
    	while(edicoesIndexados[ix][0] != null) {
    		if(edicoesIndexados[ix][0] == null) {
    			break;
    		} else {
    			InterfaceServidor.atualizaEdicaoClipada(sessao, edicoesIndexados[ix][0], edicoesIndexados[ix][1]);
    		}
    		ix++;
    	}
    }
    
    private static void limparPublicacoesLocalizadas() {
    	for(int ix=0; ix<=publicacoesLocalizadas.length-1; ix++) {
    		publicacoesLocalizadas[ix][0] = null;
    		publicacoesLocalizadas[ix][1] = null;
    	}
    }
    
    private static void gravaArquivo(String nomeArquivo, String linhas) throws IOException {
		String bufferSaida = "";
		StringBuilder bloco = new StringBuilder();
		bloco.append(linhas);
		FileWriter arqSaida = new FileWriter(nomeArquivo);
		BufferedWriter bw = new BufferedWriter(arqSaida);
		bufferSaida = bloco.toString();
		bw.write(bufferSaida);
		bw.close();
	}
}
