package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.Predmet;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.List;

@SuppressWarnings("serial")
public class PredmetiFrame extends JFrame {
	
	private PredmetiFrame ovaj;
	private List<Predmet> predmeti;

	private Predmet odabraniPredmet;

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panelPodaci;
	private JPanel panelOpcije;
	private JPanel panelPretraga;
	private JButton btnDodati;
	private JButton btnIzmeniti;
	private JButton btnObrisati;
	private JButton btnPrihvatiti;
	private JLabel lblNazivPredmeta;
	private JTextField tfNazivPredmeta;
	private JLabel lblMatiniFakultet;
	@SuppressWarnings("rawtypes")
	private JComboBox cbMaticniFakultet;
	private JButton btnPretraziti;
	private JButton btnPrikazatiSve;
	private JScrollPane scrollPane;
	private JTable table;

	/**
	 * Create the frame.
	 */
	public PredmetiFrame(boolean odabirPredmeta) {
		ovaj = this;
		predmeti = Utilities.getDataAccessFactory().getPredmetDataAccess()
				.predmeti("*", null);

		initialize();

		if (!odabirPredmeta)
			btnPrihvatiti.setVisible(false);
	}

	public Predmet getOdabraniPredmet() {
		return odabraniPredmet;
	}

	private void osveziTabelu() {
		if (Utilities.isSearchPatternValid(tfNazivPredmeta.getText())) {
			String nazivFakulteta = null;
			if (cbMaticniFakultet.getSelectedItem() != null)
				nazivFakulteta = ((Fakultet) cbMaticniFakultet
						.getSelectedItem()).getNazivFakulteta();

			predmeti = Utilities.getDataAccessFactory().getPredmetDataAccess()
					.predmeti(tfNazivPredmeta.getText(), nazivFakulteta);

			PredmetTableModel ftm = (PredmetTableModel) table.getModel();
			ftm.setPodaci(predmeti);
			ftm.fireTableDataChanged();
		}
	}

	private void initialize() {
		setTitle("Predmeti");
		setBounds(100, 100, 815, 420);
		setLocationRelativeTo(null);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
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
			panelOpcije.add(getBtnPrihvatiti());
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
			panelPretraga.add(getLblNazivPredmeta());
			panelPretraga.add(getTfNazivPredmeta());
			panelPretraga.add(getBtnPretraziti());
			panelPretraga.add(getBtnPrikazatiSve());
			panelPretraga.add(getCbMaticniFakultet());
			panelPretraga.add(getLblMatiniFakultet());
		}
		return panelPretraga;
	}

	private JButton getBtnDodati() {
		if (btnDodati == null) {
			btnDodati = new JButton("");
			btnDodati.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					PredmetDialog pd = new PredmetDialog();
					pd.setVisible(true);
					if (pd.getDialogResult().equalsIgnoreCase("OK")) {
						osveziTabelu();
						JOptionPane.showMessageDialog(ovaj,
								"Novi predmet je uspešno dodan!", "Poruka",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			btnDodati.setIcon(new ImageIcon(PredmetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Add_32.png")));
			btnDodati.setToolTipText("Dodati novi predmet");
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
								"Predmet nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						Predmet odabraniPredmet = ((PredmetTableModel) table
								.getModel()).getPredmetAtRow(table
								.getSelectedRow());
						PredmetDialog pd = new PredmetDialog(odabraniPredmet);
						pd.setVisible(true);
						if (pd.getDialogResult().equalsIgnoreCase("OK")) {
							osveziTabelu();
							JOptionPane.showMessageDialog(ovaj,
									"Predmet je uspešno ažuriran!", "Poruka",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnIzmeniti.setIcon(new ImageIcon(PredmetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Edit_32.png")));
			btnIzmeniti.setToolTipText("Izmeniti odabrani predmet");
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
								"Predmet nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						Predmet odabraniPredmet = ((PredmetTableModel) table
								.getModel()).getPredmetAtRow(table
								.getSelectedRow());
						int odabir = JOptionPane
								.showOptionDialog(
										ovaj,
										"Da li ste sigurni da želite obrisati odabrani predmet?",
										"Potvrda brisanja",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										Utilities.YES_NO_OPTIONS,
										Utilities.YES_NO_OPTIONS[1]);
						if (odabir == JOptionPane.YES_OPTION) {
							if (Utilities
									.getDataAccessFactory()
									.getPredmetDataAccess()
									.obrisiPredmet(
											odabraniPredmet.getIdPredmeta())) {
								osveziTabelu();
								JOptionPane.showMessageDialog(ovaj,
										"Predmet je uspešno obrisan!",
										"Poruka",
										JOptionPane.INFORMATION_MESSAGE);
							} else
								JOptionPane.showMessageDialog(ovaj,
										"Predmet nije uspešno obrisan!",
										"Poruka",
										JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnObrisati.setIcon(new ImageIcon(PredmetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Delete_32.png")));
			btnObrisati.setToolTipText("Obrisati odabrani predmet");
			btnObrisati.setBounds(136, 0, 58, 58);
		}
		return btnObrisati;
	}

	private JButton getBtnPrihvatiti() {
		if (btnPrihvatiti == null) {
			btnPrihvatiti = new JButton("");
			btnPrihvatiti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(ovaj,
								"Predmet nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						odabraniPredmet = ((PredmetTableModel) table.getModel())
								.getPredmetAtRow(table.getSelectedRow());
						ovaj.getToolkit()
								.getSystemEventQueue()
								.postEvent(
										new WindowEvent(ovaj,
												WindowEvent.WINDOW_CLOSING));
					}
				}
			});
			btnPrihvatiti.setIcon(new ImageIcon(PredmetiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Check_32.png")));
			btnPrihvatiti.setToolTipText("Prihvatiti odabrani predmet");
			btnPrihvatiti.setBounds(204, 0, 58, 58);
		}
		return btnPrihvatiti;
	}

	private JLabel getLblNazivPredmeta() {
		if (lblNazivPredmeta == null) {
			lblNazivPredmeta = new JLabel("Naziv predmeta:");
			lblNazivPredmeta.setBounds(10, 20, 254, 14);
		}
		return lblNazivPredmeta;
	}

	private JTextField getTfNazivPredmeta() {
		if (tfNazivPredmeta == null) {
			tfNazivPredmeta = new JTextField();
			tfNazivPredmeta.setText("*");
			tfNazivPredmeta.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
						btnPretraziti.doClick();
				}
			});
			tfNazivPredmeta.setColumns(10);
			tfNazivPredmeta.setBounds(10, 37, 254, 20);
		}
		return tfNazivPredmeta;
	}

	private JLabel getLblMatiniFakultet() {
		if (lblMatiniFakultet == null) {
			lblMatiniFakultet = new JLabel("Matični fakultet:");
			lblMatiniFakultet.setBounds(274, 20, 254, 14);
		}
		return lblMatiniFakultet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getCbMaticniFakultet() {
		if (cbMaticniFakultet == null) {
			cbMaticniFakultet = new JComboBox(Utilities.getDataAccessFactory()
					.getFakultetDataAccess().fakulteti("*")
					.toArray(new Fakultet[] {}));
			cbMaticniFakultet.setBounds(274, 37, 254, 20);
			cbMaticniFakultet.setSelectedIndex(-1);
		}
		return cbMaticniFakultet;
	}

	private JButton getBtnPretraziti() {
		if (btnPretraziti == null) {
			btnPretraziti = new JButton("Pretražiti");
			btnPretraziti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (Utilities.isSearchPatternValid(tfNazivPredmeta
							.getText()))
						osveziTabelu();
					else
						JOptionPane.showMessageDialog(ovaj,
								"Naziv predmeta nije pravilno popunjen!",
								"Greška", JOptionPane.ERROR_MESSAGE);
				}
			});
			btnPretraziti.setBounds(538, 36, 100, 23);
		}
		return btnPretraziti;
	}

	private JButton getBtnPrikazatiSve() {
		if (btnPrikazatiSve == null) {
			btnPrikazatiSve = new JButton("Prikazati sve");
			btnPrikazatiSve.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tfNazivPredmeta.setText("*");
					cbMaticniFakultet.setSelectedIndex(-1);
					osveziTabelu();
				}
			});
			btnPrikazatiSve.setBounds(648, 36, 100, 23);
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
			table = new JTable(new PredmetTableModel(predmeti));
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setFillsViewportHeight(true);
			table.getColumnModel().getColumn(0).setPreferredWidth(100);
			table.getColumnModel().getColumn(1).setPreferredWidth(250);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(250);
		}
		return table;
	}
	
}
