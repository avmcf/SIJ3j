package com.vconsulte.sij.clipping;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;

import com.vconsulte.sij.base.InterfaceServidor;  
import com.vconsulte.sij.base.*;

public class Clipping extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	static InterfaceServidor conexao = new InterfaceServidor();
	static Session sessao;
	static Folder pastaDestino;
	static File config;

	static List <String> tokensTab = new ArrayList<String>();	
	static List <String> idDocs = new ArrayList<String>();
	static List <String> indexados = new ArrayList<String>();
	static List <String> folderIds = new ArrayList<String>();
	
	static String usuario;
	static String password;
	static String pastaCarregamento;
	static String pastaPublicacoes;
	static String tipoDocumento;
	
	static String[] listaEdicoes = new String[55];
	static String[] listData = new String[55];

	static String edicaoEscolhida = "";	
	static String token = "";
    static String a = null;
    static String newline = "\n";
    static String url;
    static String caminho = "";

	static int k =0;
	static int opcao;
	
	static boolean escolheu = false;

	static JFrame frame = new JFrame("Indexação de Publicações");
	static JPanel controlPane = new JPanel();

	private JButton btn1;
    static JTextArea output;
    static JList<String> list; 
    static JTable table;
	
	static JTextField txt;	

    static ListSelectionModel listSelectionModel;

    public Clipping() {
    	super(new BorderLayout());
    	
    	btn1 = new JButton("Indexar");
    	btn1.addActionListener(this);
    	
    	txt = new JTextField(25); 
		txt.setEnabled(true);
   
        list = new JList<String>(listData);
        listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());

        JScrollPane listPane = new JScrollPane(list);
        
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Build output area.
        output = new JTextArea(1, 10);
        output.setEditable(false);
        
        JScrollPane outputPane = new JScrollPane(output,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        //Do the layout.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);

        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.PAGE_AXIS));

		// este é o list_view
        JPanel listContainer = new JPanel(new GridLayout(1,1));			// container da list_view
        listContainer.setBorder(BorderFactory.createTitledBorder("Edições localizadas"));
        listContainer.add(listPane);									// listPane é a lista de edicoes
        topHalf.add(listContainer);										// inclusão da list_view no container
        splitPane.add(topHalf);

        topHalf.add(txt);
        
        btn1.setAlignmentY(CENTER_ALIGNMENT);
        topHalf.add(btn1);

        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(controlPane, BorderLayout.PAGE_START);
        bottomHalf.add(outputPane, BorderLayout.CENTER);
        bottomHalf.setPreferredSize(new Dimension(450, 135));
        splitPane.add(bottomHalf);
    }
    
    public static void main(String[] args) throws IOException{   
    	//pastaCorrente();
    	//config = new File(caminho+"/split.cnf");
    	com.vconsulte.sij.base.Parametros.carregaTabelas();
    	com.vconsulte.sij.base.Configuracao.carregaConfig();	
		url = com.vconsulte.sij.base.Parametros.URL;
		usuario = com.vconsulte.sij.base.Parametros.USUARIO;
		password = com.vconsulte.sij.base.Parametros.PASSWORD;
		pastaCarregamento = com.vconsulte.sij.base.Parametros.PASTACARREGAMENTO;
		pastaPublicacoes = com.vconsulte.sij.base.Parametros.PASTAPUBLICACOES;
		tipoDocumento = com.vconsulte.sij.base.Parametros.TIPODOCUMENTO;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {           
        	public void run() {
        		if (!conectaServidor()) {
            		JOptionPane.showMessageDialog(null, "Falha na conexão com o servidor");
        			finalizaProcesso();
        		}
				listaEdicoes();				
            	apresentaJanela();
            }
        });
    }
    
    public static void pastaCorrente() {
		try {
			caminho = Clipping.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			caminho = caminho.substring(1, caminho.lastIndexOf('/') + 1);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

    public static void listaEdicoes() {	
		Map<String, String> mapEdicoes = new HashMap<String, String>();
		mapEdicoes = conexao.listarEdicoes(sessao, pastaCarregamento);
		carregaEdicoes(mapEdicoes);
    }

	public static boolean conectaServidor() {
		conexao.setUser(usuario);
		conexao.setPassword(password);
		conexao.setUrl(url);
		sessao = InterfaceServidor.serverConnect();
		if (sessao == null) {
			novaMensagem(obtemHrAtual() + "Falha na conexão com o servidor ");
			finalizaProcesso();
			return false;
		}
		return true;
	}
	
    public static void carregaEdicoes(Map<String, ?> edicoes) {   	
    	
    	int ix = 0;
    	Set<String> chaves = edicoes.keySet();
		for (Iterator<String> iterator = chaves.iterator(); iterator.hasNext();)
		{
			String chave = iterator.next();
			if(chave != null) {
				listData[ix] = (edicoes.get(chave).toString());
				folderIds.add(chave);
				ix++;
			}
		}
    }
    
    public static void apresentaJanela() {       
        
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Clipping demo = new Clipping();
        demo.setOpaque(true);
        frame.setContentPane(demo);
        frame.pack();
        frame.setVisible(true);
        txtMensagem("Escolha uma edição");
    }
    
	public static String obtemHrAtual() {	
		
		String hr = "";
		String mn = "";
		String sg = "";
		Calendar data = Calendar.getInstance();
		hr = Integer.toString(data.get(Calendar.HOUR_OF_DAY));
		mn = Integer.toString(data.get(Calendar.MINUTE));
		sg = Integer.toString(data.get(Calendar.SECOND));	
		return completaEsquerda(hr,'0',2) + ":" + completaEsquerda(mn,'0',2) + ":" + completaEsquerda(sg,'0',2) + " - ";
	}
	
	public static String completaEsquerda(String value, char c, int size) {
		
		String result = value;
		while (result.length() < size) {
			result = c + result;
		}
		return result;
	}
	
	public static void finalizaProcesso() {
		
		JOptionPane.showMessageDialog(null, "Fim do Processamento");
        System.exit(0);
	}
	
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {             
            opcao = e.getFirstIndex();;
            edicaoEscolhida = listData[opcao];
            if(!escolheu) {
            	txtMensagem("Edicao escolhida: " + edicaoEscolhida);
            	escolheu = true;
            }
        } 
    }
       
    public static void novaMensagem(String mensagem) {    	
    	output.append(mensagem);	
    }
    
    public static void txtMensagem(String mensagem) {
    	txt.setText(mensagem);
    }
    
    public void actionPerformed(ActionEvent evt) {				// botão indexar
		try {
			txtMensagem("Indexando: " + edicaoEscolhida);
			indexaPublicacoes();			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
    
    public static void indexaPublicacoes() throws Exception {
    	
    	String selecionados = null;
    	int indice = 0;
    	List <String> folderOrigem = new ArrayList<String>();
    	
    	novaMensagem(obtemHrAtual() +"Início da indexação");
		
		txt.setText(edicaoEscolhida);
		folderOrigem = conexao.getFolderInfo(sessao, folderIds.get(opcao));
		
		String pastaId = folderOrigem.get(0);
		String pathId = folderOrigem.get(1);
		String descricao = folderOrigem.get(2);
		String pastaNome = folderOrigem.get(3);
		String tribunal = descricao.substring(4, 6);
	
		String dummy = "";
		int kk = 0;
		
		String strEdicao = descricao.substring(23, 27) + "-";
		strEdicao = strEdicao + descricao.substring(20, 22) + "-";
		strEdicao = strEdicao + descricao.substring(17, 19);	
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        
        Date edicao = sdf.parse(strEdicao);      

    	String tokenTribunal = conexao.obtemTribunal(sessao, pastaId);
		tokensTab = conexao.carregaTokens(sessao, tokenTribunal); 	
		if(tokensTab.size() == 0) {
        	novaMensagem(obtemHrAtual() + "Não existe tokens para o TRT" + tokenTribunal);
            finalizaProcesso();
        }
        indexados.clear();

		while (indice < tokensTab.size()-1) {
			
			dummy = tokensTab.get(indice);
			System.out.println(dummy);
			if(dummy.contains(":")) {
				kk = dummy.lastIndexOf(":");
				k++;
			}
			selecionados = "SELECT d.cmis:objectId FROM sij:documento AS d JOIN sij:publicacao AS w ON d.cmis:objectId = w.cmis:objectId WHERE contains(d,'\\'" + tokensTab.get(indice) + "\\'') AND in_folder(d,'" + pastaId + "') AND w.sij:pubTribunal = '" + tribunal + "'";
		//	selecionados = "SELECT d.cmis:objectId FROM sij:documento AS d JOIN sij:publicacao AS w ON d.cmis:objectId = w.cmis:objectId WHERE contains(d,'" + tokensTab.get(indice) + "') AND in_folder(d,'" + pastaId + "') AND w.sij:pubTribunal = '" + tribunal + "'";			
			idDocs.clear();
			//idDocs = (conexao.localizaEditais(sessao, selecionados));
			k = idDocs.size();
			if(idDocs.size()>0) {
				k++;
			}
			if(idDocs.size() >0) {
				novaMensagem(obtemHrAtual() + "Localizado Token: " + tokensTab.get(indice));
				for (int i = 0; i <= idDocs.size()-1; i++) {
					InterfaceServidor.indexaPublicacao(sessao, idDocs.get(i), tokensTab.get(indice));
					if(!indexados.contains(idDocs.get(i))) {
						indexados.add(idDocs.get(i));
					}
				}
			}		
			indice++;
		}
		
		// -------------------------------------------------------------------------------------------
		
		novaMensagem(obtemHrAtual() + "Início da movimentação das publicações");
		
		if(indexados != null) {
			
			pastaDestino = InterfaceServidor.verificaPastaPublicacao(sessao, pastaPublicacoes, pastaNome + "x", descricao + " indexado", tribunal, edicao);
			
			for (int i = 0; i <= indexados.size()-1; i++) {
				InterfaceServidor.moveEditalIndexado(sessao, indexados.get(i), pathId, pastaDestino);
				novaMensagem(obtemHrAtual()+ "Publicação  " + indexados.get(i) + " movida com sucesso");
			}		
		}
		novaMensagem(obtemHrAtual() + "Processo de indexação concluído");
		escolheu = false;
		finalizaProcesso();
    }
}