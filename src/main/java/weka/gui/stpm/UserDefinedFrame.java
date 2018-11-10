/*
 * UserDefinedFrame.java
 *
 * Created on 30 de Julho de 2007, 17:31
 */

package weka.gui.stpm;

import javax.swing.*;

import weka.gui.stpm.seqpattern.Struct;
/**
 *
 * @author  Administrador
 */
public class UserDefinedFrame extends javax.swing.JFrame {
    Object[] backup;
    /** Creates new form UserDefinedFrame */
    public UserDefinedFrame() {
        initComponents();
    }
    
    public void setModel(DefaultListModel model) {
        backup = new Object[model.size()];
        model.copyInto(backup);
        jList.setModel(model);
    }
    
    private boolean check() {
        DefaultListModel model = (DefaultListModel)jList.getModel();
        Object[] array = new Object[model.size()];
        model.copyInto(array);
        for (int i=0;i<array.length;i++) {
            Struct s1 = (Struct) array[i];
            for (int j=i+1;j<array.length;j++) {
                Struct s2 = (Struct) array[j];
                if (s1.overlaps(s2))
                    return false;
            }
        }
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" C�digo Gerado ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JButton jButtonAdd;
        javax.swing.JButton jButtonCancel;
        javax.swing.JButton jButtonOk;
        javax.swing.JButton jButtonRemove;
        javax.swing.JLabel jLabel1;
        javax.swing.JLabel jLabel2;
        javax.swing.JLabel jLabel3;
        javax.swing.JLabel jLabel4;
        javax.swing.JLabel jLabel5;
        javax.swing.JLabel jLabel6;
        javax.swing.JScrollPane jScrollPane1;

        jLabel1 = new javax.swing.JLabel();
        jSpinnerFromHour = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSpinnerFromMin = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jSpinnerToHour = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jSpinnerToMin = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jButtonAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList = new javax.swing.JList();
        jButtonRemove = new javax.swing.JButton();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Intervals");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setText("From:");

        jSpinnerFromHour.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerFromHourStateChanged(evt);
            }
        });

        jLabel2.setText("Hour");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel3.setText("To:");

        jSpinnerFromMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerFromMinStateChanged(evt);
            }
        });

        jLabel4.setText("Minutes");

        jSpinnerToHour.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerToHourStateChanged(evt);
            }
        });

        jLabel5.setText("Hour");

        jSpinnerToMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerToMinStateChanged(evt);
            }
        });

        jLabel6.setText("Minutes");

        jButtonAdd.setText("Add Interval");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jList);

        jButtonRemove.setText("Remove");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSpinnerFromHour, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinnerFromMin, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSpinnerToHour, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinnerToMin)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonOk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jSpinnerFromHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jSpinnerFromMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerToHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(jSpinnerToMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)))))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(jButtonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOk)))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        DefaultListModel model = (DefaultListModel)jList.getModel();
        model.clear();
        for (Object obj: backup) {
            model.addElement(obj);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        Struct s = new Struct();
        s.fromHours = ((Integer) jSpinnerFromHour.getValue()).intValue();
        s.fromMin = ((Integer) jSpinnerFromMin.getValue()).intValue();
        s.toHours = ((Integer) jSpinnerToHour.getValue()).intValue();
        s.toMin = ((Integer) jSpinnerToMin.getValue()).intValue();
        DefaultListModel model = (DefaultListModel) jList.getModel();
        model.addElement(s);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        DefaultListModel model = (DefaultListModel)jList.getModel();
        while (!jList.isSelectionEmpty()) {
            model.remove(jList.getSelectedIndex());
        }
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        DefaultListModel model = (DefaultListModel)jList.getModel();
        model.clear();
        for (Object obj: backup) {
            model.addElement(obj);
        }
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        if (check())
            this.dispose();
        else
            JOptionPane.showMessageDialog(this,"One or more intervals are overlaped");
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jSpinnerToMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerToMinStateChanged
        int value = ((Integer) jSpinnerToMin.getValue()).intValue();
        if (value < 0)
            jSpinnerToMin.setValue(new Integer(0));
        else if (value > 59)
            jSpinnerToMin.setValue(new Integer(59));
    }//GEN-LAST:event_jSpinnerToMinStateChanged

    private void jSpinnerFromMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerFromMinStateChanged
        int value = ((Integer) jSpinnerFromMin.getValue()).intValue();
        if (value < 0)
            jSpinnerFromMin.setValue(new Integer(0));
        else if (value > 59)
            jSpinnerFromMin.setValue(new Integer(59));
    }//GEN-LAST:event_jSpinnerFromMinStateChanged

    private void jSpinnerToHourStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerToHourStateChanged
        if ( ((Integer)jSpinnerToHour.getValue()).intValue() < 0 )
            jSpinnerToHour.setValue(new Integer(0));
        else if (((Integer)jSpinnerToHour.getValue()).intValue() > 23)
            jSpinnerToHour.setValue(new Integer(23));
    }//GEN-LAST:event_jSpinnerToHourStateChanged

    private void jSpinnerFromHourStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerFromHourStateChanged
        if ( ((Integer)jSpinnerFromHour.getValue()).intValue() < 0 )
            jSpinnerFromHour.setValue(new Integer(0));
        else if (((Integer)jSpinnerFromHour.getValue()).intValue() > 23)
            jSpinnerFromHour.setValue(new Integer(23));
    }//GEN-LAST:event_jSpinnerFromHourStateChanged
    
    // Declara��o de vari�veis - n�o modifique//GEN-BEGIN:variables
    private javax.swing.JList jList;
    private javax.swing.JSpinner jSpinnerFromHour;
    private javax.swing.JSpinner jSpinnerFromMin;
    private javax.swing.JSpinner jSpinnerToHour;
    private javax.swing.JSpinner jSpinnerToMin;
    // Fim da declara��o de vari�veis//GEN-END:variables
    
}
