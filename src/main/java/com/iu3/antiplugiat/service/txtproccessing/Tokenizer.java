/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.txtproccessing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andalon
 */
public class Tokenizer {

    private List<String> text;

    public Tokenizer() {
        text = new ArrayList();
    }

    public void addText(String raw) {
        text.add(raw);
    }
    public void addText(List<String> raw){
        text.addAll(raw);
    }

    public void flush() {
        text.clear();
    }

    public List<String> getTokens() {
        List<String> tokens = new ArrayList<>();
        text.forEach(raw -> {
            String[] tokenPattern = normalizeText(raw).split(" ");
            getStemmTokens(tokenPattern, tokens);
        });
        return tokens;
    }

    private void getStemmTokens(String[] tokenPattern, List<String> tokens) {
        for (String token : tokenPattern) {
            if (!token.equals("")) {
                tokens.add(Porter.stem(token));
            }
        }
    }

    private String normalizeText(String text) {

        StringBuilder nText = new StringBuilder();
        char prev = ' ';

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDefined(c) && !Character.isISOControl(c)) {
                switch (c) {
                    case 'a':
                        nText.append("а");
                        break;
                    case 'A':
                        nText.append('А');
                    case 'B':
                        nText.append('В');
                        break;
                    case 'c':
                        nText.append('c');
                        break;
                    case 'C':
                        nText.append('С');
                        break;
                    case 'H':
                        nText.append('Н');
                        break;
                    case 'K':
                        nText.append('К');
                        break;
                    case 'M':
                        nText.append('М');
                        break;
                    case 'o':
                        nText.append('о');
                        break;
                    case 'O':
                        nText.append('О');
                        break;
                    case 'p':
                        nText.append('р');
                        break;
                    case 'P':
                        nText.append('Р');
                        break;
                    case 'T':
                        nText.append('Т');
                        break;
                    case 'x':
                        nText.append('x');
                        break;
                    case 'X':
                        nText.append('Х');
                        break;

                    case '.':
                    case ',':
                    case '!':
                    case '?':
                    case ':':
                    case ';':
                    case '\r':
                    case '\n':
                    case '(':
                    case ')':
                    case '»':
                    case '«':
                    case '\"':
                    case '\'':
                    case '_':
                    case '\t':
                    case '•':
                    case '*':
                        if (prev != ' ') {
                            prev = ' ';
                            nText.append(" ");
                        }

                        break;
                    default:
                        if (prev != ' ' || c != ' ') {
                            nText.append(c);
                            prev = c;
                        }
                }
            }
        }
        while (nText.length() != 0 && nText.charAt(nText.length() - 1) == ' ') {
            nText.deleteCharAt(nText.length() - 1);
        }
        return nText.toString().toLowerCase();
    }

}
