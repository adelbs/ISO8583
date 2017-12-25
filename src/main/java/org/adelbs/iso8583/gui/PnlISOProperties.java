package org.adelbs.iso8583.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.helper.Iso8583Config;

public class PnlISOProperties extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblDelimiter = new JLabel("Delimiter");
	private JComboBox<DelimiterEnum> cmbDelimiter = new JComboBox<DelimiterEnum>();

	private JTextArea txtDesc = new JTextArea();
	private JScrollPane scrDesc = new JScrollPane();
	
	public PnlISOProperties(final Iso8583Config isoConfig) {

		setLayout(null);
		setBorder(new TitledBorder(null, "ISO8583 Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		lblDelimiter.setBounds(12, 27, 83, 16);
		lblDelimiter.setHorizontalAlignment(SwingConstants.RIGHT);
		
		cmbDelimiter.setModel(new DefaultComboBoxModel<DelimiterEnum>(new DelimiterEnum[] {
				DelimiterEnum.LENGTH2_DELIMITER_BEG, DelimiterEnum.LENGTH4_DELIMITER_BEG, DelimiterEnum.GENERIC_CONFIG_DELIMITER}));

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
		scrDesc.setBounds(101, 59, 300, 111);
		
		add(lblDelimiter);
		add(cmbDelimiter);
		add(scrDesc);
	}
	
	public void save(Iso8583Config isoConfig) {
		isoConfig.setDelimiterEnum((DelimiterEnum) cmbDelimiter.getSelectedItem());
	}

	public void load(Iso8583Config isoConfig) {
		cmbDelimiter.setSelectedItem(isoConfig.getDelimiterEnum());
	}
}
