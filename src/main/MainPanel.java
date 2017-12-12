/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import util.ExceptionManager;
import util.Slr;

/**
 *
 * @author Usuario
 */
public class MainPanel extends javax.swing.JPanel {

    private File file = null;
    private String[] productions;
    private String[] mensagem;

    public MainPanel() {
        initComponents();
        configFields();
    }

    private void configFields() {
        fieldFileContent.setLineWrap(true);
        fieldFileContent.setWrapStyleWord(true);
        fieldFileContent.setEditable(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        fieldFileName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        buttonFindFile = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        fieldFileContent = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        messageField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        buttonAnalyze = new javax.swing.JButton();

        jLabel1.setText("Nome do Arquivo");

        buttonFindFile.setText("Encontrar");
        buttonFindFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFindFileActionPerformed(evt);
            }
        });

        fieldFileContent.setColumns(20);
        fieldFileContent.setRows(5);
        jScrollPane1.setViewportView(fieldFileContent);

        jLabel2.setText("Conteúdo do arquivo");

        jLabel3.setText("Mensagem");

        buttonAnalyze.setText("Analisar");
        buttonAnalyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAnalyzeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(messageField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldFileName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(buttonFindFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonAnalyze, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(9, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonFindFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonAnalyze))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonFindFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFindFileActionPerformed
        // TODO add your handling code here:
        String userHome = System.getProperty("user.home");
        //String penDriveJean = "G:\\Java\\Trabalho de Compiladores SLR\\";
        String penDriveJean = "/media/eduardo/9369-C039/JAVA/Trabalho de Compiladores SLR";
        JFileChooser jfc = new JFileChooser(penDriveJean);
        jfc.showOpenDialog(null);
        file = jfc.getSelectedFile();
        if (file != null) {
            fieldFileName.setText(file.getName());
            try {
                byte[] fileContentBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                String content = new String(fileContentBytes, "utf-8");
                fieldFileContent.setText(content);
                productions = content.split("\\r?\\n");

            } catch (IOException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_buttonFindFileActionPerformed

    private void buttonAnalyzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAnalyzeActionPerformed
        // TODO add your handling code here:
        try {
            validateData();
            Slr slr = new Slr(productions, messageField.getText().split(""));
            slr.printMapa();
            slr.printCanonicos();
            slr.printEstados();
            //slr.printFirst();
            //slr.printFollow();
        } catch (ExceptionManager ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }//GEN-LAST:event_buttonAnalyzeActionPerformed

    private void validateData() throws ExceptionManager {
        if (file == null) {
            throw new ExceptionManager("Favor inserir arquivo de entrada.");
        }

        if (messageField.getText().isEmpty()) {
            throw new ExceptionManager("Favor inserir mensagem de entrada.");
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAnalyze;
    private javax.swing.JButton buttonFindFile;
    private javax.swing.JTextArea fieldFileContent;
    private javax.swing.JTextField fieldFileName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField messageField;
    // End of variables declaration//GEN-END:variables
}
