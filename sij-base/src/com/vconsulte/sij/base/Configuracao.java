package com.vconsulte.sij.base;

	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.net.URISyntaxException;

//import org.apache.chemistry.opencmis.client.api.Session;

//import com.vconsulte.sij.base.*;

public class Configuracao {

	static String caminho = "";
	static File config;
	static int k = 0;
	
	
	//public static void carregaConfig(File config) throws IOException {
	public static void carregaConfig() throws IOException {

		//gravaLog(obtemHrAtual() + "Carga configuração");
		String linha = "";
		String linhaTratada = "";
		int x = 0;
		pastaCorrente();
		config = new File("/"+caminho+"split.cnf");
        FileInputStream arquivoIn = new FileInputStream(config);
		BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn), "UTF-8"));
        
        while(linha != null){
	    	linha = registro.readLine();
	    	
	    	if(linha == null) {
	    		break;
	    	} else {
	    		linhaTratada = com.vconsulte.sij.base.Comuns.formataPalavra(linha);
	    	}
	    	switch(x) {
				case 0:
					com.vconsulte.sij.base.Parametros.CLIENTE = linha;
					break;
				case 1:
					com.vconsulte.sij.base.Parametros.CONEXAO = linha;
					break;
				case 2:
					com.vconsulte.sij.base.Parametros.SYSOP = linha;
					break;
				case 3:
					com.vconsulte.sij.base.Parametros.URL = linha;
					break;
				case 4:
					com.vconsulte.sij.base.Parametros.LOGFOLDER = linha;
					break;
				case 5:
					com.vconsulte.sij.base.Parametros.PASTACARREGAMENTO = linha;
					break;
				case 6:
					com.vconsulte.sij.base.Parametros.PASTAPUBLICACOES = linha;
					break;		
				case 7:
					com.vconsulte.sij.base.Parametros.PASTATOKENS = linha;
					break;
				case 8:
					com.vconsulte.sij.base.Parametros.PASTASAIDA = linha;
					break;
				case 9:
					com.vconsulte.sij.base.Parametros.PASTAORIGEM = linha;
					break;
				case 10:
					com.vconsulte.sij.base.Parametros.PASTAEDICOES = linha;
					break;
				case 11:
					com.vconsulte.sij.base.Parametros.TIPODOCUMENTO = linha;
					break;
				case 12:
					com.vconsulte.sij.base.Parametros.TIPOPROCESSAMENTO = linha;
					break;
				case 13:
					com.vconsulte.sij.base.Parametros.TIPOARQUIVOSAIDA = linha;
					break;
				case 14:
					com.vconsulte.sij.base.Parametros.PASTADEEDICOES = linha;
				case 15:
					com.vconsulte.sij.base.Parametros.NOMEPASTACARREGAMENTO = linha;
	    	}
	    	x++;
        }
        registro.close();
	}
	
	public static void pastaCorrente() {
		try {
			caminho = Configuracao.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			caminho = caminho.substring(1, caminho.lastIndexOf('/') + 1);
			k++;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	
}