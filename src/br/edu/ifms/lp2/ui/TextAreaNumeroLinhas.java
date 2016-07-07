package br.edu.ifms.lp2.ui;

import java.awt.Color;

import javax.swing.text.Element;
import javax.swing.JTextArea;

public class TextAreaNumeroLinhas extends JTextArea {

	private static final long serialVersionUID = 3765834421294594966L;
	private JTextArea areaTexto;

	public TextAreaNumeroLinhas(JTextArea areaTexto) {
		this.areaTexto = areaTexto;
		setBackground(Color.LIGHT_GRAY);
		setEditable(false);
	}
	
	public void atualizaNumeroLinhas(){
		String textoNumeroLinhas = getTextoNumeroLinhas();
		setText(textoNumeroLinhas);
	}
	
	public String getTextoNumeroLinhas(){
		int posicaoCursor = areaTexto.getDocument().getLength();
		Element root = areaTexto.getDocument().getDefaultRootElement();
		StringBuilder construtorNumeroLinhas = new StringBuilder();
		construtorNumeroLinhas.append("1").append(System.lineSeparator());
		
		for (int indiceElemento = 2; indiceElemento < root.getElementIndex(posicaoCursor) + 2; indiceElemento++){
			construtorNumeroLinhas.append(indiceElemento).append(System.lineSeparator());
		}
		
		return construtorNumeroLinhas.toString();
	}
}
