package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;

import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@SuppressWarnings("serial")
public class FakultetiFrame extends JFrame {
	
	private FakultetiFrame ovaj;
	private List<Fakultet> fakulteti;

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panelPodaci;
	private JPanel panelOpcije;
	private JPanel panelPretraga;
	private JButton btnDodati;
	private JButton btnIzmeniti;
	private JButton btnObrisati;
	private JLabel lblNazivFakulteta;
	private JTextField tfNazivFakulteta;
	private JButton btnPretraziti;
	private JButton btnPrikazatiSve;
	private JScrollPane scrollPane;
	private JTable table;

	/**
	 * Create the frame.
	 */
	public FakultetiFrame() {
		ovaj = this;
		fakulteti = Utilities.getDataAccessFactory().getFakultetDataAccess()
				.fakulteti("*");

		initialize();
	}

	private void osveziTabelu() {
		if (Utilities.isSearchPatternValid(tfNazivFakulteta.getText())) {
			fakulteti = Utilities.getDataAccessFactory().getFakultetDataAccess()
					.fakulteti(tfNazivFakulteta.getText());

			FakultetTableModel ftm = (FakultetTableModel) table.getModel();
			ftm.setPodaci(fakulteti);
			ftm.fireTableDataChanged();
		}
	}

	private void initialize() {
		setTitle("Fakulteti");
		setBounds(100, 100, 815, 420);
		setLocationRelativeTo(null);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));
		this.contentPane.add(getPanel(), BorderLayout.NORTH);
		this.contentPane.add(getPanelPodaci(), BorderLayout.CENTER);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getPanelOpcije(), BorderLayout.NORTH);
			panel.add(getPanelPretraga(), BorderLayout.SOUTH);
		}
		return panel;
	}

	private JPanel getPanelPodaci() {
		if (panelPodaci == null) {
			panelPodaci = new JPanel();
			panelPodaci.setLayout(new BorderLayout(0, 0));
			panelPodaci.add(getScrollPane(), BorderLayout.CENTER);
		}
		return panelPodaci;
	}

	private JPanel getPanelOpcije() {
		if (panelOpcije == null) {
			panelOpcije = new JPanel();
			panelOpcije.setLayout(null);
			panelOpcije.setPreferredSize(new Dimension(200, 68));
			panelOpcije.add(getBtnDodati());
			panelOpcije.add(getBtnIzmeniti());
			panelOpcije.add(getBtnObrisati());
		}
		return panelOpcije;
	}

	private JPanel getPanelPretraga() {
		if (panelPretraga == null) {
			panelPretraga = new JPanel();
			panelPretraga.setBorder(new TitledBorder(null, "Pretraga",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panelPretraga.setLayout(null);
			panelPretraga.setPreferredSize(new Dimension(200, 70));
			panelPretraga.add(getLblNazivFakulteta());
			panelPretraga.add(getTfNazivFakulteta());
			panelPretraga.add(getBtnPretraziti());
			panelPretraga.add(getBtnPrikazatiSve());
		}
		return panelPretraga;
	}

	private JButton getBtnDodati() {
		if (btnDodati == null) {
			btnDodati = new JButton("");
			btnDodati.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FakultetDialog fd = new FakultetDialog();
					fd.setVisible(true);
					if (fd.getDialogResult().equalsIgnoreCase("OK")) {
						osveziTabelu();
						JOptionPane.showMessageDialog(ovaj,
								"Novi fakultet je uspešno dodan!", "Poruka",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			btnDodati.setToolTipText("Dodati novi fakultet");
			btnDodati.setIcon(new ImageIcon(FakultetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Add_32.png")));
			btnDodati.setBounds(0, 0, 58, 58);
		}
		return btnDodati;
	}

	private JButton getBtnIzmeniti() {
		if (btnIzmeniti == null) {
			btnIzmeniti = new JButton("");
			btnIzmeniti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(ovaj,
								"Fakultet nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						Fakultet odabraniFakultet = ((FakultetTableModel) table
								.getModel()).getFakultetAtRow(table
								.getSelectedRow());
						FakultetDialog fd = new FakultetDialog(odabraniFakultet);
						fd.setVisible(true);
						if (fd.getDialogResult().equalsIgnoreCase("OK")) {
							osveziTabelu();
							JOptionPane.showMessageDialog(ovaj,
									"Fakultet je uspešno ažuriran!", "Poruka",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnIzmeniti.setToolTipText("Izmeniti odabrani fakultet");
			btnIzmeniti.setIcon(new ImageIcon(FakultetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Edit_32.png")));
			btnIzmeniti.setBounds(68, 0, 58, 58);
		}
		return btnIzmeniti;
	}

	private JButton getBtnObrisati() {
		if (btnObrisati == null) {
			btnObrisati = new JButton("");
			btnObrisati.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(ovaj,
								"Fakultet nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						Fakultet odabraniFakultet = ((FakultetTableModel) table
								.getModel()).getFakultetAtRow(table
								.getSelectedRow());
						int odabir = JOptionPane
								.showOptionDialog(
										ovaj,
										"Da li ste sigurni da želite obrisati odabrani fakultet?",
										"Potvrda brisanja",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										Utilities.YES_NO_OPTIONS,
										Utilities.YES_NO_OPTIONS[1]);
						if (odabir == JOptionPane.YES_OPTION) {
							if (Utilities
									.getDataAccessFactory()
									.getFakultetDataAccess()
									.obrisiFakultet(
											odabraniFakultet
													.getNazivFakulteta())) {
								osveziTabelu();
								JOptionPane.showMessageDialog(ovaj,
										"Fakultet je uspešno obrisan!",
										"Poruka",
										JOptionPane.INFORMATION_MESSAGE);
							} else
								JOptionPane.showMessageDialog(ovaj,
										"Fakultet nije uspešno obrisan!",
										"Poruka",
										JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnObrisati.setToolTipText("Obrisati odabrani fakultet");
			btnObrisati.setIcon(new ImageIcon(FakultetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Delete_32.png")));
			btnObrisati.setBounds(136, 0, 58, 58);
		}
		return btnObrisati;
	}

	private JLabel getLblNazivFakulteta() {
		if (lblNazivFakulteta == null) {
			lblNazivFakulteta = new JLabel("Naziv fakulteta:");
			lblNazivFakulteta.setBounds(10, 21, 254, 14);
		}
		return lblNazivFakulteta;
	}

	private JTextField getTfNazivFakulteta() {
		if (tfNazivFakulteta == null) {
			tfNazivFakulteta = new JTextField();
			tfNazivFakulteta.setText("*");
			tfNazivFakulteta.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
						btnPretraziti.doClick();
				}
			});
			tfNazivFakulteta.setBounds(10, 38, 254, 20);
			tfNazivFakulteta.setColumns(10);
		}
		return tfNazivFakulteta;
	}

	private JButton getBtnPretraziti() {
		if (btnPretraziti == null) {
			btnPretraziti = new JButton("Pretražiti");
			btnPretraziti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (Utilities.isSearchPatternValid(tfNazivFakulteta
							.getText()))
						osveziTabelu();
					else
						JOptionPane.showMessageDialog(ovaj,
								"Naziv fakulteta nije pravilno popunjen!",
								"Greška", JOptionPane.ERROR_MESSAGE);
				}
			});
			btnPretraziti.setBounds(274, 37, 100, 23);
		}
		return btnPretraziti;
	}

	private JButton getBtnPrikazatiSve() {
		if (btnPrikazatiSve == null) {
			btnPrikazatiSve = new JButton("Prikazati sve");
			btnPrikazatiSve.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tfNazivFakulteta.setText("*");
					osveziTabelu();
				}
			});
			btnPrikazatiSve.setBounds(384, 37, 100, 23);
		}
		return btnPrikazatiSve;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setViewportView(getTable());
		}
		return scrollPane;
	}

	private JTable getTable() {
		if (table == null) {
			table = new JTable(new FakultetTableModel(fakulteti));
			table.setFillsViewportHeight(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getColumnModel().getColumn(0).setPreferredWidth(300);
			table.getColumnModel().getColumn(1).setPreferredWidth(450);
		}
		return table;
	}
	
}
