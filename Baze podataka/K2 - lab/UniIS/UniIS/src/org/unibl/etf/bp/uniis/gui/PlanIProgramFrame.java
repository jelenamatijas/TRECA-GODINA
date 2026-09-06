package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.border.TitledBorder;

import org.unibl.etf.bp.uniis.entity.PredmetNaStudijskomProgramu;
import org.unibl.etf.bp.uniis.entity.StudijskiProgram;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PlanIProgramFrame extends JFrame {
	
	private PlanIProgramFrame ovaj;
	private List<PredmetNaStudijskomProgramu> predmetiNaSP;

	private StudijskiProgram studijskiProgram;

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panelPodaci;
	private JPanel panelOpcije;
	private JPanel panelPretraga;
	private JButton btnDodati;
	private JButton btnIzmeniti;
	private JButton btnObrisati;
	private JLabel lblStudijskiProgram;
	private JTextField tfIdSP;
	private JButton btnOdaberiSP;
	private JTextField tfStudijskiProgram;
	private JButton btnPretraziti;
	private JScrollPane scrollPane;
	private JTable table;

	/**
	 * Create the frame.
	 */
	public PlanIProgramFrame() {
		ovaj = this;
		predmetiNaSP = new ArrayList<PredmetNaStudijskomProgramu>();

		initialize();
	}

	private void pronadjiStudijskiProgram(boolean tiho) {
		tfStudijskiProgram.setText("");
		studijskiProgram = null;
		ocistiTabelu();

		if (Utilities.tryParseInt(tfIdSP.getText())) {
			studijskiProgram = Utilities.getDataAccessFactory()
					.getStudijskiProgramDataAccess()
					.studijskiProgram(Integer.valueOf(tfIdSP.getText()));
			if (studijskiProgram != null)
				tfStudijskiProgram.setText(studijskiProgram.toString());
		} else if (!tiho)
			JOptionPane
					.showMessageDialog(
							ovaj,
							"Identifikator studijskog programa nije pravilno popunjen!",
							"Greška", JOptionPane.ERROR_MESSAGE);
	}

	private void osveziTabelu() {
		if (studijskiProgram != null) {
			predmetiNaSP = Utilities.getDataAccessFactory()
					.getPredmetNaStudijskomProgramuDataAccess()
					.predmetiNaSP(studijskiProgram.getIdSP());

			PlanIProgramTableModel ftm = (PlanIProgramTableModel) table
					.getModel();
			ftm.setPodaci(predmetiNaSP);
			ftm.fireTableDataChanged();
		}
	}

	private void ocistiTabelu() {
		predmetiNaSP = new ArrayList<PredmetNaStudijskomProgramu>();

		PlanIProgramTableModel ftm = (PlanIProgramTableModel) table.getModel();
		ftm.setPodaci(predmetiNaSP);
		ftm.fireTableDataChanged();
	}

	private void initialize() {
		setTitle("Plan i program");
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
			panelOpcije.setPreferredSize(new Dimension(200, 68));
			panelOpcije.setLayout(null);
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
			panelPretraga.setPreferredSize(new Dimension(200, 70));
			panelPretraga.setLayout(null);
			panelPretraga.add(getLblStudijskiProgram());
			panelPretraga.add(getTfIdSP());
			panelPretraga.add(getBtnOdaberiSP());
			panelPretraga.add(getTfStudijskiProgram());
			panelPretraga.add(getBtnPretraziti());
		}
		return panelPretraga;
	}

	private JButton getBtnDodati() {
		if (btnDodati == null) {
			btnDodati = new JButton("");
			btnDodati.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (studijskiProgram == null) {
						JOptionPane.showMessageDialog(ovaj,
								"Studijski program nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						final PlanIProgramDialog ppd = new PlanIProgramDialog(
								studijskiProgram);
						ppd.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosing(WindowEvent e) {
								if (ppd.getDialogResult()
										.equalsIgnoreCase("OK")) {
									osveziTabelu();
									JOptionPane
											.showMessageDialog(
													ovaj,
													"Predmet je uspešno dodan na studijski program!",
													"Poruka",
													JOptionPane.INFORMATION_MESSAGE);
								}
							}
						});
						ppd.setVisible(true);
					}
				}
			});
			btnDodati.setIcon(new ImageIcon(PlanIProgramFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Add_32.png")));
			btnDodati
					.setToolTipText("Dodati novi predmet na odabrani studijski program");
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
						PredmetNaStudijskomProgramu odabraniPredmetNaSP = ((PlanIProgramTableModel) table
								.getModel())
								.getPredmetNaStudijskomProgramuAtRow(table
										.getSelectedRow());
						final PlanIProgramDialog ppd = new PlanIProgramDialog(
								odabraniPredmetNaSP);
						ppd.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosing(WindowEvent e) {
								if (ppd.getDialogResult()
										.equalsIgnoreCase("OK")) {
									osveziTabelu();
									JOptionPane.showMessageDialog(ovaj,
											"Predmet je uspešno ažuriran!",
											"Poruka",
											JOptionPane.INFORMATION_MESSAGE);
								}
							}
						});
						ppd.setVisible(true);
					}
				}
			});
			btnIzmeniti.setIcon(new ImageIcon(PlanIProgramFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Edit_32.png")));
			btnIzmeniti
					.setToolTipText("Izmeniti odabrani predmet na studijskom programu");
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
						PredmetNaStudijskomProgramu odabraniPredmetNaSP = ((PlanIProgramTableModel) table
								.getModel())
								.getPredmetNaStudijskomProgramuAtRow(table
										.getSelectedRow());
						int odabir = JOptionPane
								.showOptionDialog(
										ovaj,
										"Da li ste sigurni da želite obrisati odabrani predmet sa studijskog programa?",
										"Potvrda brisanja",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										Utilities.YES_NO_OPTIONS,
										Utilities.YES_NO_OPTIONS[1]);
						if (odabir == JOptionPane.YES_OPTION) {
							if (Utilities
									.getDataAccessFactory()
									.getPredmetNaStudijskomProgramuDataAccess()
									.obrisiPredmetNaSP(
											odabraniPredmetNaSP.getPredmet()
													.getIdPredmeta(),
											odabraniPredmetNaSP
													.getStudijskiProgram()
													.getIdSP())) {
								osveziTabelu();
								JOptionPane
										.showMessageDialog(
												ovaj,
												"Predmet je uspešno obrisan sa studijskog programa!",
												"Poruka",
												JOptionPane.INFORMATION_MESSAGE);
							} else
								JOptionPane
										.showMessageDialog(
												ovaj,
												"Predmet nije uspešno obrisan sa studijskog programa!",
												"Poruka",
												JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnObrisati.setIcon(new ImageIcon(PlanIProgramFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Delete_32.png")));
			btnObrisati
					.setToolTipText("Obrisati odabrani predmet sa studijskog programa");
			btnObrisati.setBounds(136, 0, 58, 58);
		}
		return btnObrisati;
	}

	private JLabel getLblStudijskiProgram() {
		if (lblStudijskiProgram == null) {
			lblStudijskiProgram = new JLabel("Studijski program:");
			lblStudijskiProgram.setBounds(10, 22, 125, 14);
		}
		return lblStudijskiProgram;
	}

	private JTextField getTfIdSP() {
		if (tfIdSP == null) {
			tfIdSP = new JTextField();
			tfIdSP.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent arg0) {
					pronadjiStudijskiProgram(true);
				}
			});
			tfIdSP.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
						pronadjiStudijskiProgram(false);
				}
			});
			tfIdSP.setColumns(10);
			tfIdSP.setBounds(10, 39, 125, 20);
		}
		return tfIdSP;
	}

	private JButton getBtnOdaberiSP() {
		if (btnOdaberiSP == null) {
			btnOdaberiSP = new JButton("");
			btnOdaberiSP.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ovaj.setVisible(false);
					final StudijskiProgramiFrame spf = new StudijskiProgramiFrame(
							true);
					spf.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							if (spf.getOdabraniStudijskiProgram() != null) {
								ocistiTabelu();

								studijskiProgram = spf
										.getOdabraniStudijskiProgram();
								tfIdSP.setText(Integer
										.toString(studijskiProgram.getIdSP()));
								tfStudijskiProgram.setText(studijskiProgram
										.toString());
							}
							spf.dispose();
							ovaj.setVisible(true);
							ovaj.toFront();
						}
					});
					spf.setVisible(true);
				}
			});
			btnOdaberiSP.setIcon(new ImageIcon(PlanIProgramFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Lookup_14.png")));
			btnOdaberiSP.setBounds(145, 38, 30, 23);
		}
		return btnOdaberiSP;
	}

	private JTextField getTfStudijskiProgram() {
		if (tfStudijskiProgram == null) {
			tfStudijskiProgram = new JTextField();
			tfStudijskiProgram.setEditable(false);
			tfStudijskiProgram.setBounds(185, 39, 300, 20);
			tfStudijskiProgram.setColumns(10);
		}
		return tfStudijskiProgram;
	}

	private JButton getBtnPretraziti() {
		if (btnPretraziti == null) {
			btnPretraziti = new JButton("Pretražiti");
			btnPretraziti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (studijskiProgram == null) {
						JOptionPane.showMessageDialog(ovaj,
								"Studijski program nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else
						osveziTabelu();
				}
			});
			btnPretraziti.setBounds(495, 36, 100, 23);
		}
		return btnPretraziti;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTable());
		}
		return scrollPane;
	}

	private JTable getTable() {
		if (table == null) {
			table = new JTable(new PlanIProgramTableModel(predmetiNaSP));
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setFillsViewportHeight(true);
			table.getColumnModel().getColumn(0).setPreferredWidth(100);
			table.getColumnModel().getColumn(1).setPreferredWidth(300);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(50);
		}
		return table;
	}
	
}
