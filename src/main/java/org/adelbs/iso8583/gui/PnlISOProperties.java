package org.adelbs.iso8583.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.helper.Iso8583Config;

public class PnlISOProperties extends JPanel {

	private static final long serialVersionUID = 2L;
	
	private JLabel lblDelimiter = new JLabel("Delimiter");
	private JComboBox<DelimiterEnum> cmbDelimiter = new JComboBox<DelimiterEnum>();

	private JTextArea txtDesc = new JTextArea();
	private JScrollPane scrDesc = new JScrollPane();
    
    private JLabel lblHeaderSize = new JLabel("Header Size");
    private JTextField txtHeaderSize = new JTextField();

    private JLabel lblHeaderEncoding = new JLabel("Header Encoding");
    private JComboBox<EncodingEnum> cmbHeaderEncoding = new JComboBox<EncodingEnum>();

    private JLabel lblTPDU = new JLabel("TPDU");
    private JCheckBox chkTPDU = new JCheckBox();

    
	public PnlISOProperties(final Iso8583Config isoConfig) {

		setLayout(null);
		setBorder(new TitledBorder(null, "ISO8583 Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		lblDelimiter.setBounds(12, 27, 83, 16);
		lblDelimiter.setHorizontalAlignment(SwingConstants.RIGHT);
		
		cmbDelimiter.setModel(new DefaultComboBoxModel<DelimiterEnum>(new DelimiterEnum[] {DelimiterEnum.LENGTH2_DELIMITER_BEG, DelimiterEnum.GENERIC_CONFIG_DELIMITER}));

		cmbDelimiter.setBounds(101, 24, 300, 22);
		cmbDelimiter.setSelectedIndex(0);
		cmbDelimiter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtDesc.setText(((DelimiterEnum) cmbDelimiter.getSelectedItem()).getDelimiter().getDesc());
				save(isoConfig);
			}
		});

		txtDesc.setEditable(false);
		txtDesc.setLineWrap(true);
		scrDesc.setViewportView(txtDesc);
		scrDesc.setBounds(101, 59, 300, 81);
		

        lblHeaderSize.setBounds(12, 160, 83, 16);
        lblHeaderSize.setHorizontalAlignment(SwingConstants.RIGHT);

        txtHeaderSize.setBounds(101, 157, 50, 22);
        txtHeaderSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(isoConfig);
			}
		});

        lblHeaderEncoding.setBounds(160, 160, 100, 16);
        lblHeaderEncoding.setHorizontalAlignment(SwingConstants.RIGHT);

        cmbHeaderEncoding.setBounds(270, 157, 132, 22);
        EncodingEnum.addCmbItemList(cmbHeaderEncoding);
        cmbHeaderEncoding.setSelectedIndex(0);
        cmbHeaderEncoding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(isoConfig);
			}
		});
        
        lblTPDU.setBounds(12, 190, 83, 16);
        lblTPDU.setHorizontalAlignment(SwingConstants.RIGHT);
        
        chkTPDU.setBounds(101,190,20,20);
        chkTPDU.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(isoConfig);
			}
		});
        
        add(lblDelimiter);
		add(cmbDelimiter);
		add(scrDesc);
		add(lblHeaderSize);
		add(txtHeaderSize);
		add(lblHeaderEncoding);
		add(cmbHeaderEncoding);
		add(lblTPDU);
		add(chkTPDU);
		
		
	}
	
	public void save(Iso8583Config isoConfig) {
        isoConfig.setDelimiterEnum((DelimiterEnum) cmbDelimiter.getSelectedItem());
        isoConfig.setHeaderEncoding((EncodingEnum) cmbHeaderEncoding.getSelectedItem());
        if (!txtHeaderSize.getText().trim().equals("")) isoConfig.setHeaderSize(Integer.parseInt(txtHeaderSize.getText()));
        else isoConfig.setHeaderSize(0);
        
        isoConfig.setTPDU(chkTPDU.isSelected());
        
	}

	public void load(Iso8583Config isoConfig) {
        cmbDelimiter.setSelectedItem(isoConfig.getDelimiterEnum());
        cmbHeaderEncoding.setSelectedItem(isoConfig.getHeaderEncoding());
        txtHeaderSize.setText(String.valueOf(isoConfig.getHeaderSize()));
        
        chkTPDU.setSelected((Boolean)isoConfig.getTPDU());
        
        
	}
}
