/*******************************************************************************
 * Copyright (c) 2010 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/

/* SITube Downloader is a simple project started to learn more things about JAVA.
 * A big thanks go to all the JAVA community and the developer(s) of  JAVE 	 and 
 * ffmepg that this software use to manage the conversion of the downloaded video. 
 */


import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.VideoAttributes;


import javax.swing.SwingUtilities;


import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JFrame;

import javax.swing.JTextField;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;


import javax.swing.JButton;
import javax.swing.JComboBox;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;



public class Main extends JFrame implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="44,23"
	private ExtendedTextField txtUrl = null;
	private JTextField txtSave = null;
	private JButton cmdBrowse = null;
	private JComboBox cmbConvert = null;//changed
	private JComboBox cmbSource = null;//changed
	private JProgressBar jProgressBar = null;
	private JLabel picture = null; 
	private JButton cmdStart = null;
	private JButton cmdExit = null;
	private Task task ;  //  @jve:decl-index=0:
	private ArrayList<COptions> options;  //  @jve:decl-index=0:
	private Youtube video;
	private JCheckBox chkConvert = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private String filename;
	private String cfilename;
	private String appDir = System.getProperty("user.dir");  //  @jve:decl-index=0:
	

	class Task extends SwingWorker<Void, Void> {


		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() throws IOException, InterruptedException {

			double progress = 0;
			//Initialize progress property.*/
			setProgress(0);

			double max = 0;

			if (video.getLinks().get( Youtube.fmtList.get(cmbSource.getSelectedItem()))==null){
					JOptionPane.showMessageDialog(null, "Resolution not available for this video");
					return null;
			}
			
			System.out.println("--->"+video.getLinks().get( Youtube.fmtList.get(cmbSource.getSelectedItem())));
			URL download = new URL (video.getLinks().get( Youtube.fmtList.get(cmbSource.getSelectedItem())));
			try {
				max = download.openConnection().getContentLength();

				System.out.println(max);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedInputStream in = new BufferedInputStream(download.openStream());
			FileOutputStream fos = new FileOutputStream(filename);
			BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			int oneChar;
			
			while ((oneChar=in.read()) != -1){
				if (isCancelled()) {
					setProgress(0);
					break;
				}
		
				
				fos.write(oneChar);
				progress++;
				setProgress((int) (progress*100/max));
			}
			in.close();
			fos.close();
			
			bout.close();
			in.close();
			if (chkConvert.isSelected()) {
				COptions opt = options.get(cmbConvert.getSelectedIndex());
				cfilename = txtSave.getText()+File.separator; //download path		
				cfilename += video.getVideoTitle()+"."+options.get(cmbConvert.getSelectedIndex()).getExt(); // dl path + filename + ext
				String acodec = opt.getAcodec();
				String channel = opt.getChannel();
				String frequency = opt.getFrequency();
				File source = new File(filename);
				File target = new File(cfilename);
				AudioAttributes audio = new AudioAttributes();
				audio.setCodec(acodec);
				audio.setChannels(Integer.parseInt(channel));
				audio.setSamplingRate(Integer.parseInt(frequency));
				VideoAttributes videoa = null;
				EncodingAttributes attrs = new EncodingAttributes();		
				attrs.setFormat(opt.getExt());
				attrs.setAudioAttributes(audio);
				if (!opt.getCategory().equalsIgnoreCase("audio")) {
					videoa = new VideoAttributes();
					videoa.setCodec(opt.getVcodec());
					if (opt.getTag() != null) { 
						videoa.setTag(opt.getTag());;
						
					}
					
					attrs.setVideoAttributes(videoa);
				}
				
				Encoder encoder = new Encoder();
				
				try {
					encoder.encode(source, target, attrs);

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					showError("Conversion failed. Illegal Argument Exception");


				} catch (InputFormatException e) {
					e.printStackTrace();
					showError("Conversion failed. Input Exception");

				} catch (EncoderException e) {
					showError("Conversion failed. " + e.getMessage());
					e.printStackTrace();
				}

				source.delete(); 

			}



			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {

			Toolkit.getDefaultToolkit().beep();
			cmdExit.setEnabled(false);

		}
	}

	/**
	 * This method initializes txtUrl	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	
	private ExtendedTextField getTxtUrl() {
		if (txtUrl == null) {
			txtUrl = new ExtendedTextField();
			txtUrl.setBounds(16, 107, 455, 24);
			txtUrl.setToolTipText("Insert a Youtube Video Link");
		}
		return txtUrl;
	}

	/**
	 * This method initializes txtSave	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtSave() {
		if (txtSave == null) {
			txtSave = new JTextField();
			txtSave.setBounds(16, 164, 547, 24);
		}
		return txtSave;
	}

	/**
	 * This method initializes cmdBrowse	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdBrowse() {
		if (cmdBrowse == null) {
			cmdBrowse = new JButton();
			cmdBrowse.setBounds(583, 153, 49, 35);
			cmdBrowse.setToolTipText("Choose where the video will be saved");
			cmdBrowse.setIcon(new ImageIcon(getClass().getResource("/folder_open-48.png")));
			cmdBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File dir;
					JFileChooser fc = new JFileChooser(appDir);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

					int rc = fc.showDialog(null, "Select Download Directory");

					if (rc == JFileChooser.APPROVE_OPTION){
						
						dir = fc.getSelectedFile();

						String dirname = dir.getAbsolutePath();
						txtSave.setText(dirname);
					}
					
					return;
				}

			});
		}
		return cmdBrowse;
	}

	/**
	 * This method initializes cmbConvert	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCmbConvert()//changed 3
	{
		if (cmbConvert == null) {
			cmbConvert = new JComboBox();
			cmbConvert.setBounds(new Rectangle(402, 195, 161, 20));
			cmbConvert.setToolTipText("Select the output format");
			cmbConvert.setVisible(false);
			options = new Options(getClass().getClassLoader().getResourceAsStream("presets.xml")).read();
			for (int i = 0;i<options.size();i++)
				cmbConvert.addItem(options.get(i).getName());

		}
		return cmbConvert;
	}

	/**
	 * This method initializes cmbSource	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getCmbSource() {
		
		if (cmbSource == null) {
			cmbSource = new JComboBox();
			cmbSource.setModel(new DefaultComboBoxModel(new String[] {"3GP - 176x144", "FLV - 400x240", "FLV - 480x270", "FLV - 640x360", "FLV - 854x480", "MP4 - 640x360", "MP4 - 1280x720", "MP4 - 1920x1080", "MP4 - 4096x3072", "WEBM - 640x360", "WEBM - 854x480", "WEBM - 1280x720"}));
			cmbSource.setBounds(481, 107, 149, 24);
			cmbSource.setToolTipText("Select the quality of the Youtube Video");
/*			HashMap<String,String> options = Youtube.fmtList;
			Iterator<Entry<String, String>> it = options.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
				cmbSource.addItem((pairs.getKey()));
			}*/
		}

		return cmbSource;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(16, 239, 616, 22);
			jProgressBar.setStringPainted(true);
			jProgressBar.setValue(0);


			jProgressBar.setVisible(false);
		}
		return jProgressBar;
	}

	/**
	 * This method initializes cmdStart	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdStart() {
		if (cmdStart == null) {
			cmdStart = new JButton();
			cmdStart.setBounds(388, 272, 114, 68);
			cmdStart.setIcon(new ImageIcon(getClass().getResource("/Download_64.png")));
			cmdStart.addActionListener(this);
		}

		return cmdStart;
	}

	private JLabel getpicture() {
		if (picture == null) {
			picture =new JLabel(new ImageIcon(getClass().getResource("/head.png")));
			picture.setBounds(0, 0, 654, 74);
		}
		return picture;
	}
	/**
	 * This method initializes cmdExit	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdExit() {
		if (cmdExit == null) {
			cmdExit = new JButton();
			cmdExit.setBounds(new Rectangle(514, 272, 114, 68));
			cmdExit.setEnabled(false);
			cmdExit.setIcon(new ImageIcon(getClass().getResource("/Cancel2-64.png")));
			cmdExit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					task.cancel(true);
				}
			});
		}
		return cmdExit;
	}

	/**
	 * This method initializes chkConvert	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkConvert() {
		if (chkConvert == null) {
			chkConvert = new JCheckBox();
			chkConvert.setBounds(12, 196, 192, 21);
			chkConvert.setText("Convert after download");
			chkConvert.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (chkConvert.isSelected()) {
						cmbConvert.setVisible(true);
					}else {
						cmbConvert.setVisible(false);
					}

				}
			});
		}
		return chkConvert;
	}



	public static void main(String[] args)  {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException e1) {
			
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			
			e1.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main thisClass = new Main();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 * @throws IOException 
	 */
	public Main() {
		super();
		initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 * @throws IOException 
	 */
	private void initialize() {
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		this.setSize(new Dimension(654, 383));
		this.setContentPane(getJContentPane());
		Dimension dim = toolkit.getScreenSize();
		this.setLocation(new Point((dim.width-this.getWidth())/2, (dim.height-this.getHeight())/2));
		this.setResizable(false);
		this.setTitle("SITube Downloader");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 * @throws IOException 
	 */
	private JPanel getJContentPane(){
		if (jContentPane == null) {
			jLabel2 = new JLabel();
			jLabel2.setBounds(16, 142, 174, 16);
			jLabel2.setText("Select Download Directory");
			jLabel1 = new JLabel();
			jLabel1.setBounds(481, 85, 149, 16);
			jLabel1.setText("Select Download Format");
			jLabel = new JLabel();
			jLabel.setBounds(16, 85, 126, 16);
			jLabel.setText("Insert Youtube Link");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setLayout(null);
			jContentPane.add(getTxtUrl(), null);
			jContentPane.add(getTxtSave(), null);
			jContentPane.add(getCmdBrowse(), null);
			jContentPane.add(getCmbConvert(), null);
			jContentPane.add(getCmbSource(), null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(getCmdStart(), null);
			jContentPane.add(getCmdExit(), null);
			jContentPane.add(getChkConvert(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getTxtUrl(), null);
			jContentPane.add(getTxtSave(), null);
			jContentPane.add(getCmdBrowse(), null);
			jContentPane.add(getCmbConvert(), null);
			jContentPane.add(getCmbSource(), null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(getCmdStart(), null);
			jContentPane.add(getCmdExit(), null);
			jContentPane.add(getChkConvert(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getpicture(), null);
		}
		return jContentPane;
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {

		if (task.isCancelled()) { //you press cmdExit, the (partial) downloaded video is deleted and also the partial converted file (if exists)
			jProgressBar.setString("Aborted");
			new File(filename).delete();
			if ((cfilename != null ) && (new File(cfilename).exists())) 
				new File(cfilename).delete();
			cmdExit.setEnabled(false);

		}
		if(task.isDone()) { // all the operations are done
			jProgressBar.setIndeterminate(false);
			jProgressBar.setValue(100);
			jProgressBar.setString("Complete");
		}/*else {
			jProgressBar.setString("Failed.. Try to change the quality");
			cmdExit.setEnabled(false);
		}*/
		if ("progress" == e.getPropertyName()) {

			int progress = (Integer) e.getNewValue();

			if ((progress>=100) && (!task.isDone())) { //download complete but task isn't done.. so is converting..
				jProgressBar.setIndeterminate(true);
				jProgressBar.setString("Converting..");
			} else
			if (!task.isDone() && progress != 100) { //is downloading the video..
				jProgressBar.setValue(progress);
				jProgressBar.setString(Integer.toString(progress)+"%");           
			} else

			if (task.isCancelled()) { //you press cmdExit, the (partial) downloaded video is deleted and also the partial converted file (if exists)
				jProgressBar.setString("Aborted");
				new File(filename).delete();
				if ((cfilename != null ) && (new File(cfilename).exists())) new File(cfilename).delete();
				cmdExit.setEnabled(false);

			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) { //if no youtube link is inserted..
		
		if (txtUrl.getText().equals("") || !txtUrl.getText().contains("youtube.com/watch?v=")) {
			showError("Insert a Valid Youtube Video Link");
			txtUrl.requestFocus();
			return;

		}
		if (txtSave.getText().equals("")) { //if no download directory is choosen set the  app directory..

			txtSave.setText(appDir+File.separator+"Download");
			if (!new File(appDir+File.separator+"Download").exists()) new File(appDir+File.separator+"Download").mkdir();
			

		}

		video = new Youtube(txtUrl.getText());//,(String) cmbSource.getSelectedItem());
		try {
			video.getLinks();
		} catch (UnsupportedEncodingException e2) {

			e2.printStackTrace();
		}


		try {
			filename = txtSave.getText()+File.separator+video.getVideoTitle()+video.getExt(Youtube.fmtList.get(cmbSource.getSelectedItem()));
			jProgressBar.setString("Retrieving Video Details");
		} catch (UnsupportedEncodingException e1) {
			
			e1.printStackTrace();
		}



		if (new File(filename).exists()) { //if the video was already downloaded..
			int response;
			response = JOptionPane.showConfirmDialog(null, filename + " already exists. Do you want to overwrite it?");
			if (response == 1 || response == 2)
				return;
		}
		
		cmdExit.setEnabled(true);
		task = new Task();
		jProgressBar.setVisible(true);
		task.addPropertyChangeListener(this);
		task.execute();	//start the download
	}

	
	private void showError(String message) {
		JOptionPane.showMessageDialog(null, message);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
