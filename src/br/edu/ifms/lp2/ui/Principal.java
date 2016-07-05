package br.edu.ifms.lp2.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.awt.event.InputEvent;
import javax.swing.SwingConstants;

public class Principal {

	private JFrame janela;
	private static JTextArea areaTexto;
	private JScrollPane scrollPane;
	private JToolBar barraFerramentas, barraStatus;
	private JTextField textoBarraStatus;
	private TextAreaNumeroLinhas numeroLinhas;
	
	private static File arquivo;
	private static String texto;
	private static String nomeArquivo;
	private static boolean textoSalvo;
	
	private final ImageIcon iconeNovo = new ImageIcon("icons/new.png");
	private final ImageIcon iconeAbrir= new ImageIcon("icons/open.png");
	private final ImageIcon iconeSalvar = new ImageIcon("icons/save.png");
	private final ImageIcon iconeSalvarComo = new ImageIcon("icons/save-as.png");
	
	private final ImageIcon iconeRecortar = new ImageIcon("icons/cut.png");
	private final ImageIcon iconeCopiar = new ImageIcon("icons/copy.png");
	private final ImageIcon iconeColar = new ImageIcon("icons/paste.png");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principal window = new Principal();
					window.janela.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Principal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		textoSalvo = true;
		nomeArquivo = "Sem título";
		
		janela = new JFrame();
		janela.setBounds(100, 100, 450, 300);
		janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		janela.getContentPane().setLayout(new BorderLayout(0, 0));
		janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
		
		janela.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				if (verificaMudanca(arquivo, janela, textoSalvo))
					janela.dispose();
			}
		});
		
		areaTexto = new JTextArea();
		
		areaTexto.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				int numeroLinha = 1;
				int numeroColuna = 1;
				
				try {
					int posicaoCursor = areaTexto.getCaretPosition();
					numeroLinha = areaTexto.getLineOfOffset(posicaoCursor);
					numeroColuna = posicaoCursor - areaTexto.getLineOfOffset(numeroLinha);
					numeroLinha++;
					
				} catch (Exception e){}
				
				atualizaStatus(numeroLinha, numeroColuna);
				
			}
		});
		janela.getContentPane().add(areaTexto, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane(areaTexto);
		janela.getContentPane().add(scrollPane);
		
		numeroLinhas = new TextAreaNumeroLinhas(areaTexto);
		scrollPane.setRowHeaderView(numeroLinhas);
		
		areaTexto.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e){
				numeroLinhas.atualizaNumeroLinhas();
				if (nomeArquivo.charAt(0) != '*' && !textoSalvo){
					nomeArquivo = "*" + nomeArquivo;
					janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
				}
				textoSalvo = false;
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				numeroLinhas.atualizaNumeroLinhas();
				if (nomeArquivo.charAt(0) != '*' && !textoSalvo){
					nomeArquivo = "*" + nomeArquivo;
					janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
				}
				textoSalvo = false;
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.println("changedUpdate");

			}
		});
		
		JMenuBar barraMenu = new JMenuBar();
		janela.setJMenuBar(barraMenu);
		
		JMenu menuArquivo = new JMenu("Arquivo");
		barraMenu.add(menuArquivo);
		
		JMenuItem menuItemNovo = new JMenuItem("Novo");
		menuItemNovo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuItemNovo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				novoArquivo(janela);
			}
		});
		menuArquivo.add(menuItemNovo);
		
		JMenuItem menuItemAbrir = new JMenuItem("Abrir...");
		menuItemAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuItemAbrir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				abrir(janela);
			}
		});
		menuArquivo.add(menuItemAbrir);
		
		JMenuItem menuItemSalvar = new JMenuItem("Salvar");
		menuItemSalvar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuItemSalvar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvar(janela);
			}
		});
		menuArquivo.add(menuItemSalvar);
		
		JMenuItem menuItemSalvarComo = new JMenuItem("Salvar Como...");
		menuItemSalvarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuItemSalvarComo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvarComo(janela);
			}
		});
		menuArquivo.add(menuItemSalvarComo);
		
		JMenuItem menuItemFechar = new JMenuItem("Fechar");
		menuItemFechar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		menuItemFechar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (verificaMudanca(arquivo, janela, textoSalvo))
					janela.dispose();
			}
		});
		menuArquivo.add(menuItemFechar);
		
		JMenu menuEditar = new JMenu("Editar");
		barraMenu.add(menuEditar);
		
		JMenuItem menuItemCopiar = new JMenuItem(new DefaultEditorKit.CopyAction());
		menuItemCopiar.setText("Copiar");
		menuItemCopiar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		menuEditar.add(menuItemCopiar);
		
		JMenuItem menuItemColar = new JMenuItem(new DefaultEditorKit.PasteAction());
		menuItemColar.setText("Colar");
		menuItemColar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		menuEditar.add(menuItemColar);
		
		JMenuItem menuItemRecortar = new JMenuItem(new DefaultEditorKit.CutAction());
		menuItemRecortar.setText("Recortar");
		menuItemRecortar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		menuEditar.add(menuItemRecortar);
		
		JMenu menuExibir = new JMenu("Exibir");
		barraMenu.add(menuExibir);
		
		JCheckBoxMenuItem chkboxExibirFerramentas = new JCheckBoxMenuItem();
		chkboxExibirFerramentas.setText("Barra de Ferramentas");
		chkboxExibirFerramentas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barraFerramentas.setVisible(chkboxExibirFerramentas.getState());
			}
		});
		menuExibir.add(chkboxExibirFerramentas);
		
		JCheckBoxMenuItem chkboxExibirBarraStatus = new JCheckBoxMenuItem();
		chkboxExibirBarraStatus.setText("Barra de Status");
		chkboxExibirBarraStatus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				barraStatus.setVisible(chkboxExibirBarraStatus.getState());
			}
		});
		menuExibir.add(chkboxExibirBarraStatus);
		
		JMenu menuSobre = new JMenu("Sobre");
		barraMenu.add(menuSobre);
		
		barraFerramentas = new JToolBar();
		barraFerramentas.setFloatable(false);
		barraFerramentas.setVisible(false);
		janela.getContentPane().add(barraFerramentas, BorderLayout.NORTH);
		
		JButton btnNovo = new JButton(iconeNovo);
		btnNovo.setToolTipText("Cria um arquivo em branco.");
		btnNovo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				novoArquivo(janela);
			}
		});
		barraFerramentas.add(btnNovo);
		
		JButton btnAbrir = new JButton(iconeAbrir);
		btnAbrir.setToolTipText("Abre um arquivo.");
		btnAbrir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abrir(janela);
			}
		});
		barraFerramentas.add(btnAbrir);
		
		JButton btnSalvar = new JButton(iconeSalvar);
		btnSalvar.setToolTipText("Salva o arquivo atual.");
		btnSalvar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvar(janela);
			}
		});
		barraFerramentas.add(btnSalvar);
		
		JButton btnSalvarComo = new JButton(iconeSalvarComo);
		btnSalvarComo.setToolTipText("Salva o arquivo atual em outro arquivo.");
		btnSalvarComo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvarComo(janela);
			}
		});
		barraFerramentas.add(btnSalvarComo);
		
		barraFerramentas.addSeparator(new Dimension(20, 0));
		
		JButton btnCopiar = new JButton(iconeCopiar);
		btnCopiar.setToolTipText("Copia o texto selecionado.");
		btnCopiar.addActionListener(new DefaultEditorKit.CopyAction());
		barraFerramentas.add(btnCopiar);
		
		JButton btnColar = new JButton(iconeColar);
		btnColar.setToolTipText("Cola o texto da área de transferência.");
		btnColar.addActionListener(new DefaultEditorKit.PasteAction());
		barraFerramentas.add(btnColar);
		
		JButton btnRecortar = new JButton(iconeRecortar);
		btnRecortar.setToolTipText("Recorta o testo selecionado.");
		btnRecortar.addActionListener(new DefaultEditorKit.CutAction());
		barraFerramentas.add(btnRecortar);
		
		textoBarraStatus = new JTextField();
		textoBarraStatus.setHorizontalAlignment(SwingConstants.CENTER);
		textoBarraStatus.setEditable(false);
		
		barraStatus = new JToolBar();
		barraStatus.setSize(new Dimension(18, 4));
		barraStatus.setFloatable(false);
		barraStatus.setVisible(false);
		janela.getContentPane().add(barraStatus, BorderLayout.SOUTH);
		
		atualizaStatus(1, 1);
		barraStatus.add(textoBarraStatus);
		
	}
	
	public void atualizaStatus (int numeroLinha, int numeroColuna){
		textoBarraStatus.setText("Linha: " + numeroLinha + " | Coluna: " + numeroColuna);
	}
	
	public static boolean verificaMudanca(File arquivo, JFrame janela, boolean textoSalvo){
		if (!textoSalvo){
			int resposta = JOptionPane.showConfirmDialog(janela, "Deseja salvar as alterações?");
			if (resposta == JOptionPane.YES_OPTION){
				if (arquivo == null){
					JFileChooser janelaArquivo = new JFileChooser();
					janelaArquivo.showSaveDialog(janela);
					arquivo = janelaArquivo.getSelectedFile();
				}
				if (arquivo != null){
					texto = areaTexto.getText();
					textoSalvo = salvaArquivo(arquivo, texto);
					return true;
				}

			} else if (resposta == JOptionPane.CANCEL_OPTION)
				return false;
		}
		return true;
	}
	
	public static void novoArquivo (JFrame janela){
		if (verificaMudanca(arquivo, janela, textoSalvo)){
			arquivo = null;
			areaTexto.setText("");
			textoSalvo = true;
			nomeArquivo = "Sem título";
			janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
		}
	}
	
	public static boolean abreArquivo(File arquivo){
		try {
			Scanner leitor = new Scanner(arquivo);
			texto = "";
			
			while (leitor.hasNextLine()){
				texto += leitor.nextLine() + "\n";

			}
			leitor.close();
			
			areaTexto.setText(texto);
			nomeArquivo = arquivo.getName();
			return true;
			
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void abrir(JFrame janela){
		if (verificaMudanca(arquivo, janela, textoSalvo)){
			JFileChooser janelaArquivo = new JFileChooser();
			int opcao = janelaArquivo.showOpenDialog(janela);
			if (opcao == JFileChooser.APPROVE_OPTION){
				arquivo = janelaArquivo.getSelectedFile();
				if (arquivo != null){
					nomeArquivo = arquivo.getName();
					textoSalvo = abreArquivo(arquivo);
					janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
				}
			}
		};
	}
	
	public static boolean salvaArquivo(File arquivo, String texto){
		try {
			FileWriter gravador = new FileWriter(arquivo, false);
			gravador.write(texto);
			gravador.close();
			nomeArquivo = arquivo.getName();
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void salvar(JFrame janela){
		if (arquivo == null){
			JFileChooser janelaArquivo = new JFileChooser();
			janelaArquivo.showSaveDialog(janela);
			arquivo = janelaArquivo.getSelectedFile();
		}
		if (arquivo != null){
			texto = areaTexto.getText();
			textoSalvo = salvaArquivo(arquivo, texto);
			nomeArquivo = arquivo.getName();
			janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
		}
	}
	
	public static void salvarComo(JFrame janela){
		JFileChooser janelaArquivo = new JFileChooser();
		janelaArquivo.showSaveDialog(janela);
		arquivo = janelaArquivo.getSelectedFile();
		if (arquivo != null){
			texto = areaTexto.getText();
			textoSalvo = salvaArquivo(arquivo, texto);
			nomeArquivo = arquivo.getName();
			janela.setTitle(nomeArquivo + " - MUETX (Mais Um Editor de TeXto)");
		}
	}
}
