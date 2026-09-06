package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.bp.uniis.util.Utilities;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class GlavniProzor extends JFrame {
	
	private JFrame ovaj;

	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu mnAplikacija;
	private JMenuItem mntmIzlaz;
	private JMenu mnSifarnici;
	private JMenuItem mntmFakulteti;
	private JMenuItem mntmPredmeti;
	private JMenuItem mntmStudijskiProgrami;
	private JMenu mnPlanIProgram;
	private JMenuItem mntmPlanIProgram;
	private JMenu mnIzvestaji;
	private JMenuItem mntmProsecneOceneStudenata;

	/**
	 * Create the frame.
	 */
	public GlavniProzor() {
		initialize();
	}

	private void initialize() {
		ovaj = this;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				int rezultat = JOptionPane.showOptionDialog(ovaj,
						"Da li ste sigurni da želite zatvoriti aplikaciju?",
						"Potvrda zatvaranja", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null,
						Utilities.YES_NO_OPTIONS,
						Utilities.YES_NO_OPTIONS[1]);
				if (rezultat == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		setTitle("UniIS - Univerzitetski informacioni sistem");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 815, 420);
		setLocationRelativeTo(null);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setJMenuBar(getMenuBar_1());

		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
	}

	private JMenuBar getMenuBar_1() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBar.add(getMnAplikacija());
			menuBar.add(getMnSifarnici());
			menuBar.add(getMnPlanIProgram());
			menuBar.add(getMnIzvestaji());
		}
		return menuBar;
	}

	private JMenu getMnAplikacija() {
		if (mnAplikacija == null) {
			mnAplikacija = new JMenu("Aplikacija");
			mnAplikacija.setMnemonic('A');
			mnAplikacija.add(getMntmIzlaz());
		}
		return mnAplikacija;
	}

	private JMenuItem getMntmIzlaz() {
		if (mntmIzlaz == null) {
			mntmIzlaz = new JMenuItem("Izlaz");
			mntmIzlaz.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ovaj.getToolkit()
							.getSystemEventQueue()
							.postEvent(
									new WindowEvent(ovaj,
											WindowEvent.WINDOW_CLOSING));
				}
			});
			mntmIzlaz.setMnemonic('I');
			mntmIzlaz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					InputEvent.ALT_MASK));
		}
		return mntmIzlaz;
	}

	private JMenu getMnSifarnici() {
		if (mnSifarnici == null) {
			mnSifarnici = new JMenu("Šifarnici");
			mnSifarnici.setMnemonic('F');
			mnSifarnici.add(getMntmFakulteti());
			mnSifarnici.add(getMntmPredmeti());
			mnSifarnici.add(getMntmStudijskiProgrami());
		}
		return mnSifarnici;
	}

	private JMenuItem getMntmFakulteti() {
		if (mntmFakulteti == null) {
			mntmFakulteti = new JMenuItem("Fakulteti...");
			mntmFakulteti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new FakultetiFrame().setVisible(true);
				}
			});
			mntmFakulteti.setMnemonic('F');
		}
		return mntmFakulteti;
	}

	private JMenuItem getMntmPredmeti() {
		if (mntmPredmeti == null) {
			mntmPredmeti = new JMenuItem("Predmeti...");
			mntmPredmeti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new PredmetiFrame(false).setVisible(true);
				}
			});
			mntmPredmeti.setMnemonic('P');
		}
		return mntmPredmeti;
	}

	private JMenuItem getMntmStudijskiProgrami() {
		if (mntmStudijskiProgrami == null) {
			mntmStudijskiProgrami = new JMenuItem("Studijski programi...");
			mntmStudijskiProgrami.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new StudijskiProgramiFrame(false).setVisible(true);
				}
			});
			mntmStudijskiProgrami.setMnemonic('S');
		}
		return mntmStudijskiProgrami;
	}

	private JMenu getMnPlanIProgram() {
		if (mnPlanIProgram == null) {
			mnPlanIProgram = new JMenu("Plan i program");
			mnPlanIProgram.setMnemonic('P');
			mnPlanIProgram.add(getMntmPlanIProgram());
		}
		return mnPlanIProgram;
	}

	private JMenuItem getMntmPlanIProgram() {
		if (mntmPlanIProgram == null) {
			mntmPlanIProgram = new JMenuItem("Plan i program");
			mntmPlanIProgram.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new PlanIProgramFrame().setVisible(true);
				}
			});
			mntmPlanIProgram.setMnemonic('P');
		}
		return mntmPlanIProgram;
	}

	private JMenu getMnIzvestaji() {
		if (mnIzvestaji == null) {
			mnIzvestaji = new JMenu("Izveštaji");
			mnIzvestaji.setMnemonic('I');
			mnIzvestaji.add(getMntmProsecneOceneStudenata());
		}
		return mnIzvestaji;
	}

	private JMenuItem getMntmProsecneOceneStudenata() {
		if (mntmProsecneOceneStudenata == null) {
			mntmProsecneOceneStudenata = new JMenuItem(
					"Prosečne ocene studenata...");
			mntmProsecneOceneStudenata.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new IzvestajiFrame().setVisible(true);
				}
			});
			mntmProsecneOceneStudenata.setMnemonic('P');
		}
		return mntmProsecneOceneStudenata;
	}
	
}
