package com.AweiMC.PalServerTool.gui.pange;

import com.AweiMC.PalServerTool.Main;
import com.AweiMC.PalServerTool.backup.Backup;
import com.AweiMC.PalServerTool.config.Config;
import com.AweiMC.PalServerTool.config.PTSConfig;
import com.AweiMC.PalServerTool.gui.InitGUI;
import com.AweiMC.PalServerTool.ini.INIConfig;
import com.AweiMC.PalServerTool.ini.IniExporter;
import com.AweiMC.PalServerTool.ini.PalServerConfig;
import com.AweiMC.PalServerTool.util.DeathPenaltyEnum;
import com.AweiMC.PalServerTool.util.DifficultyEnum;
import com.AweiMC.PalServerTool.util.I18nManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.AweiMC.PalServerTool.config.Config.saveConfig;

public class ConfigPage {
    public static JPanel page = new JPanel(null);

    public ConfigPage(JPanel page) {
        ConfigPage.page = page;
    }
    private static void outINIFile(String path) {
        Backup.checkFile(path,true);
        IniExporter.exportToIniFile(PalServerConfig.CONFIG_MAP, path);

    }
    public static void addPage() {
        int BTextX = 180;
        int Tw = 200;
        int Y = 10;
        int TBTextX = BTextX + 290 - 4;
        int VBTextX = BTextX + 600 - 4;
        int LY = Y+400;
        addText();


        DefaultListModel<DifficultyEnum> listModel = new DefaultListModel<>();
        listModel.addAll(Arrays.asList(DifficultyEnum.values()));

        JList<DifficultyEnum> EList = new JList<>(listModel);
        EList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        diffEnumSet(EList);

        DefaultListModel<DeathPenaltyEnum> dpM = new DefaultListModel<>();
        dpM.addAll(Arrays.asList(DeathPenaltyEnum.values()));

        JList<DeathPenaltyEnum> DPList = new JList<>(dpM);
        DPList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dpEnumSet(DPList);

        JScrollPane scrollPane = new JScrollPane(EList);
        scrollPane.setBounds(BTextX, Y, Tw-100, 18);

        JScrollPane dpPane = new JScrollPane(DPList);
        dpPane.setBounds(BTextX + 290, Y+156, Tw-100, 18);

        JButton setIniButton = new JButton(I18nManager.getMessage("message.ini.save"));
        sButton(setIniButton);

        setIniButton.setBounds(870,490,Tw-90,18);

        JTextField PATH = new JTextField(cfg.iniFilePath);
        PATH.setBounds(VBTextX-240,LY+80,Tw+10,18);

        setIniButton.addActionListener(e-> {
            String pth = PATH.getText();

            outINIFile(pth);
        });

        JButton genDefaultConfig = new JButton(I18nManager.getMessage("message.ini.gen.default"));
        genDefaultConfig.setBounds(870,490-33,Tw-90,18);
        genDefaultConfig.addActionListener(e -> exportDefaultConfig());

        JButton chooseButton = new JButton(I18nManager.getMessage("message.backup.path.set"));
        chooseButton.setToolTipText(I18nManager.getMessage("message.backup.path.set.tooltip"));
        chooseButton.setBounds(VBTextX-20,LY+80,Tw-100,18);//选择目录
        chooseButton.addActionListener(e -> {
            File iniFile = chooseFile(page);
            if (iniFile != null) {
                cfg.iniFilePath = String.valueOf(iniFile);
                saveConfig(cfg);
                PATH.setText(String.valueOf(iniFile));
                INIConfig.init();
                // 刷新整个页面
                page.revalidate();
                page.repaint();
                refreshPage(setIniButton);
            }
        });
        page.add(genDefaultConfig);
        page.add(PATH);
        page.add(chooseButton);
        page.add(dpPane);

        page.add(setIniButton);
        page.add(scrollPane);
    }
    private static void exportDefaultConfig() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveDirectory = fileChooser.getSelectedFile();
            try {
                exportConfigToDirectory(saveDirectory.toPath());
                JOptionPane.showMessageDialog(null, "Config exported successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error exporting config: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private static String fileTimeText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return dateFormat.format(new Date());
    }
    private static void exportConfigToDirectory(Path targetDirectory) throws IOException {
        String time = "#" + I18nManager.getMessage("message.ini.gen.time") + fileTimeText();
        String info = "#" + String.format(I18nManager.getMessage("message.ini.gen.info"),Main.Name);

        // 构造配置内容
        List<String> configLines = Arrays.asList(
                time,
                info,
                "[/Script/Pal.PalGameWorldSettings]",
                "OptionSettings=(Difficulty=None,DayTimeSpeedRate=1.000000,NightTimeSpeedRate=1.000000,ExpRate=1.000000,PalCaptureRate=1.000000,PalSpawnNumRate=1.000000,PalDamageRateAttack=1.000000,PalDamageRateDefense=1.000000,PlayerDamageRateAttack=1.000000,PlayerDamageRateDefense=1.000000,PlayerStomachDecreaceRate=1.000000,PlayerStaminaDecreaceRate=1.000000,PlayerAutoHPRegeneRate=1.000000,PlayerAutoHpRegeneRateInSleep=1.000000,PalStomachDecreaceRate=1.000000,PalStaminaDecreaceRate=1.000000,PalAutoHPRegeneRate=1.000000,PalAutoHpRegeneRateInSleep=1.000000,BuildObjectDamageRate=1.000000,BuildObjectDeteriorationDamageRate=1.000000,CollectionDropRate=1.000000,CollectionObjectHpRate=1.000000,CollectionObjectRespawnSpeedRate=1.000000,EnemyDropItemRate=1.000000,DeathPenalty=All,bEnablePlayerToPlayerDamage=False,bEnableFriendlyFire=False,bEnableInvaderEnemy=True,bActiveUNKO=False,bEnableAimAssistPad=True,bEnableAimAssistKeyboard=False,DropItemMaxNum=3000,DropItemMaxNum_UNKO=100,BaseCampMaxNum=128,BaseCampWorkerMaxNum=15,DropItemAliveMaxHours=1.000000,bAutoResetGuildNoOnlinePlayers=False,AutoResetGuildTimeNoOnlinePlayers=72.000000,GuildPlayerMaxNum=20,PalEggDefaultHatchingTime=72.000000,WorkSpeedRate=1.000000,bIsMultiplay=False,bIsPvP=False,bCanPickupOtherGuildDeathPenaltyDrop=False,bEnableNonLoginPenalty=True,bEnableFastTravel=True,bIsStartLocationSelectByMap=True,bExistPlayerAfterLogout=False,bEnableDefenseOtherGuildPlayer=False,CoopPlayerMaxNum=4,ServerPlayerMaxNum=32,ServerName=\"Default Palworld Server\",ServerDescription=\"\",AdminPassword=\"1232132\",ServerPassword=\"\",PublicPort=8211,PublicIP=\"\",RCONEnabled=False,RCONPort=25575,Region=\"\",bUseAuth=True,BanListURL=\"https://api.palworldgame.com/api/banlist.txt\")"
        );

        // 将配置写入文件
        try {
            Files.write(targetDirectory.resolve("PalWorldSettings.ini"), configLines);
            System.out.println("Config written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // TODO This addText
    private static void addText() {
        int TextX = 10;
        int TTextX = TextX+285;
        int VTextX = TTextX+285;
        int Th = 20;
        int Tw = 160;
        int Y = 10;
        var DF = new JLabel(I18nManager.getMessage("message.ini.dif"));
        DF.setBounds(TextX, Y, Tw, Th);
        var PTSR = new JLabel(I18nManager.getMessage("message.ini.ptsr"));
        PTSR.setBounds(TextX,Y+23,Tw,Th);
        var NTSR = new JLabel(I18nManager.getMessage("message.ini.ntsr"));
        NTSR.setBounds(TextX,Y+45,Tw,Th);
        var EXPR = new JLabel(I18nManager.getMessage("message.ini.expr"));
        EXPR.setBounds(TextX,Y+67,Tw,Th);
        var PCR = new JLabel(I18nManager.getMessage("message.ini.pcr"));
        PCR.setBounds(TextX,Y+89,Tw,Th);
        var PSNR = new JLabel(I18nManager.getMessage("message.ini.psnr"));
        PSNR.setBounds(TextX,Y+108,Tw,Th);
        var PDRA = new JLabel(I18nManager.getMessage("message.ini.pdra"));
        PDRA.setBounds(TextX,Y+130,Tw,Th);
        var PDRD = new JLabel(I18nManager.getMessage("message.ini.pdrd"));
        PDRD.setBounds(TextX,Y+152,Tw,Th);
        var PDRAK = new JLabel(I18nManager.getMessage("message.ini.pdrak"));
        PDRAK.setBounds(TextX,Y+175,Tw,Th);
        var PDRDE = new JLabel(I18nManager.getMessage("message.ini.pdrde"));
        PDRDE.setBounds(TextX,Y+197,Tw,Th);
        var PSDR = new JLabel(I18nManager.getMessage("message.ini.psdr"));
        PSDR.setBounds(TextX,Y+220,Tw,Th);
        var PSADR = new JLabel(I18nManager.getMessage("message.ini.psadr"));
        PSADR.setBounds(TextX,Y+242,Tw,Th);
        var PHPRR = new JLabel(I18nManager.getMessage("message.ini.pahprr"));
        PHPRR.setBounds(TextX,Y+265,Tw,Th);
        var PAHRRS = new JLabel(I18nManager.getMessage("message.ini.pahrrs"));
        PAHRRS.setBounds(TextX,Y+287,Tw,Th);
        var PADRE = new JLabel(I18nManager.getMessage("message.ini.padre"));
        PADRE.setBounds(TextX,Y+310,Tw,Th);
        var PSDRE = new JLabel(I18nManager.getMessage("message.ini.psdre"));
        PSDRE.setBounds(TextX,Y+332,Tw,Th);
        var PLAHRR = new JLabel(I18nManager.getMessage("message.ini.plahrr"));
        PLAHRR.setBounds(TextX,Y+355,Tw,Th);
        var PLAHRRS = new JLabel(I18nManager.getMessage("message.ini.plahrrs"));
        PLAHRRS.setBounds(TTextX,Y-2,Tw,Th);
        var BODR = new JLabel(I18nManager.getMessage("message.ini.bodr"));
        BODR.setBounds(TTextX,Y+20,Tw,Th);
        var BODDR = new JLabel(I18nManager.getMessage("message.ini.boddr"));
        BODDR.setBounds(TTextX,Y+45,Tw,Th);
        var CDR = new JLabel(I18nManager.getMessage("message.ini.cdr"));
        CDR.setBounds(TTextX,Y+67,Tw,Th);
        var CODR = new JLabel(I18nManager.getMessage("message.ini,cohr"));
        CODR.setBounds(TTextX,Y+89,Tw,Th);
        var CORSR = new JLabel(I18nManager.getMessage("message.ini.corsr"));
        CORSR.setBounds(TTextX,Y+111,Tw,Th);
        var EDIR = new JLabel(I18nManager.getMessage("message.ini.edir"));
        EDIR.setBounds(TTextX,Y+134,Tw,Th);
        var DP = new JLabel(I18nManager.getMessage("message.ini.dp"));
        DP.setBounds(TTextX,Y+156,Tw,Th);
        var BEPTPD = new JLabel(I18nManager.getMessage("message.ini.beptpd"));
        BEPTPD.setBounds(TTextX,Y+178,Tw,Th);
        var BEFF = new JLabel(I18nManager.getMessage("message.ini.beff"));
        BEFF.setBounds(TTextX,Y+199,Tw,Th);
        var BEIE = new JLabel(I18nManager.getMessage("message.ini.beie"));
        BEIE.setBounds(TTextX,Y+220,Tw,Th);
        var BAUNKO = new JLabel(I18nManager.getMessage("message.ini.baunko"));
        BAUNKO.setBounds(TTextX,Y+242,Tw,Th);
        var BEAAP = new JLabel(I18nManager.getMessage("message.ini.beaap"));
        BEAAP.setBounds(TTextX,Y+265,Tw,Th);
        var BEAAK = new JLabel(I18nManager.getMessage("message.ini.beaak"));
        BEAAK.setBounds(TTextX,Y+287,Tw,Th);
        var DRMN = new JLabel(I18nManager.getMessage("message.ini.dimn"));
        DRMN.setBounds(TTextX,Y+313,Tw,Th);
        var DRMNU = new JLabel(I18nManager.getMessage("message.ini.drmnu"));
        DRMNU.setBounds(TTextX,Y+335,Tw,Th);
        var BCMN = new JLabel(I18nManager.getMessage("message.ini.bcmn"));
        BCMN.setBounds(TTextX,Y+356,Tw,Th);
        var BCWMN = new JLabel(I18nManager.getMessage("message.ini.bcwmn"));
        BCWMN.setBounds(VTextX,Y-2,Tw,Th);
        var DRMH = new JLabel(I18nManager.getMessage("message.ini.diamh"));
        DRMH.setBounds(VTextX,Y+20,Tw,Th);
        var BRNOP = new JLabel(I18nManager.getMessage("message.ini.bargnop"));
        BRNOP.setBounds(VTextX,Y+42,Tw+20,Th);
        var ARGT = new JLabel(I18nManager.getMessage("message.ini.argt"));
        ARGT.setBounds(VTextX,Y+65,Tw+30,Th);
        var GPMH = new JLabel(I18nManager.getMessage("message.ini.gpmn"));
        GPMH.setBounds(VTextX,Y+88,Tw,Th);
        var PEDH = new JLabel(I18nManager.getMessage("message.ini.pedht"));
        PEDH.setBounds(VTextX,Y+110,Tw,Th);
        var WSR = new JLabel(I18nManager.getMessage("message.ini.wsr"));
        WSR.setBounds(VTextX,Y+132,Tw,Th);
        var BIM = new JLabel(I18nManager.getMessage("message.ini.bim"));
        BIM.setBounds(VTextX,Y+155,Tw,Th);
        var BIP = new JLabel(I18nManager.getMessage("message.ini.bip"));
        BIP.setBounds(VTextX,Y+178,Tw,Th);
        var BCPO = new JLabel(I18nManager.getMessage("message.ini.bcpo"));
        BCPO.setBounds(VTextX,Y+200,Tw+20,Th);
        var BENL = new JLabel(I18nManager.getMessage("message.ini.benl"));
        BENL.setBounds(VTextX,Y+222,Tw+20,Th);
        var BEF = new JLabel(I18nManager.getMessage("message.ini.bef"));
        BEF.setBounds(VTextX,Y+245,Tw+20,Th);
        var BSL = new JLabel(I18nManager.getMessage("message.ini.bsl"));
        BSL.setBounds(VTextX,Y+267,Tw+20,Th);
        var BEA = new JLabel(I18nManager.getMessage("message.ini.bea"));
        BEA.setBounds(VTextX,Y+290,Tw+20,Th);
        var BEDO = new JLabel(I18nManager.getMessage("message.ini.bedo"));
        BEDO.setBounds(VTextX,Y+312,Tw+20,Th);
        var CPMN = new JLabel(I18nManager.getMessage("message.ini.cpmn"));
        CPMN.setBounds(VTextX,Y+335,Tw+20,Th);
        var SPMN = new JLabel(I18nManager.getMessage("message.ini.spmn"));
        int LY = Y+400;
        SPMN.setBounds(TextX,LY,Tw+20,Th);
        var SN = new JLabel(I18nManager.getMessage("message.ini.sn"));
        SN.setBounds(TextX,LY+22,Tw+20,Th);
        var SD = new JLabel(I18nManager.getMessage("message.ini.sd"));
        SD.setBounds(TextX,LY+45,Tw+20,Th);
        var AP = new JLabel(I18nManager.getMessage("message.ini.ap"));
        AP.setBounds(TTextX-60,LY,Tw+20,Th);
        var SP = new JLabel(I18nManager.getMessage("message.ini.sp"));
        SP.setBounds(TTextX-60,LY+22,Tw+20,Th);
        var PP = new JLabel(I18nManager.getMessage("message.ini.pp"));
        PP.setBounds(TTextX-60,LY+45,Tw+20,Th);
        var PIP = new JLabel(I18nManager.getMessage("message.ini.pip"));
        PIP.setBounds(VTextX-160,LY,Tw+20,Th);
        var RCONE = new JLabel(I18nManager.getMessage("message.ini.rcone"));
        RCONE.setBounds(VTextX-160,LY+22,Tw+20,Th);
        var RCONP = new JLabel(I18nManager.getMessage("message.ini.rconp"));
        RCONP.setBounds(VTextX-160,LY+45,Tw+20,Th);
        var RGI = new JLabel(I18nManager.getMessage("message.ini.rgi"));
        RGI.setBounds(VTextX+30,LY,Tw+20,Th);
        var BUA = new JLabel(I18nManager.getMessage("message.ini.bua"));
        BUA.setBounds(VTextX+30,LY+22,Tw+20,Th);
        var BIU = new JLabel(I18nManager.getMessage("message.ini.biu"));
        BIU.setBounds(VTextX+30,LY+45,Tw+20,Th);
        page.add(BIU);
        page.add(BUA);
        page.add(RGI);
        page.add(RCONP);
        page.add(RCONE);
        page.add(PIP);
        page.add(PP);
        page.add(SP);
        page.add(AP);
        page.add(SD);
        page.add(SN);
        page.add(SPMN);
        page.add(CPMN);
        page.add(BEDO);
        page.add(BEA);
        page.add(BSL);
        page.add(BEF);
        page.add(BENL);
        page.add(BCPO);
        page.add(BIP);
        page.add(BIM);
        page.add(WSR);
        page.add(PEDH);
        page.add(GPMH);
        page.add(ARGT);
        page.add(BRNOP);
        page.add(DRMH);
        page.add(BCWMN);
        page.add(BCMN);
        page.add(DRMNU);
        page.add(DRMN);
        page.add(BEAAK);
        page.add(BEAAP);
        page.add(BAUNKO);
        page.add(BEIE);
        page.add(BEFF);
        page.add(BEPTPD);
        page.add(DP);
        page.add(EDIR);
        page.add(CORSR);
        page.add(CODR);
        page.add(CDR);
        page.add(BODDR);
        page.add(BODR);
        page.add(PLAHRRS);
        page.add(PLAHRR);
        page.add(PSDRE);
        page.add(PADRE);
        page.add(PAHRRS);
        page.add(PHPRR);
        page.add(PSADR);
        page.add(PSDR);
        page.add(PDRDE);
        page.add(PDRAK);
        page.add(PDRD);
        page.add(PDRA);
        page.add(PSNR);
        page.add(PCR);
        page.add(NTSR);
        page.add(PTSR);
        page.add(DF);
        page.add(EXPR);
    }
    private static void sButton(JButton submitButton) {
        int BTextX = 180;
        int TBTextX = BTextX + 290;
        int VBTextX = BTextX + 600;
        int Tw = 200;
        int Y = 10;
        JTextField DRSR = new JTextField(PalServerConfig.getConfigValue("DayTimeSpeedRate"));
        DRSR.setBounds(BTextX,Y+23,Tw-100,18);
        JTextField NTSR = new JTextField(PalServerConfig.getConfigValue("NightTimeSpeedRate"));
        NTSR.setBounds(BTextX,Y+45,Tw-100,18);
        JTextField EXPR = new JTextField(PalServerConfig.getConfigValue("ExpRate"));
        EXPR.setBounds(BTextX,Y+67,Tw-100,18);
        JTextField PCR = new JTextField(PalServerConfig.getConfigValue("PalCaptureRate"));
        PCR.setBounds(BTextX,Y+89,Tw-100,18);
        JTextField PSNR = new JTextField(PalServerConfig.getConfigValue("PalSpawnNumRate"));
        PSNR.setBounds(BTextX,Y+110,Tw-100,18);
        JTextField PDRA = new JTextField(PalServerConfig.getConfigValue("PalDamageRateAttack"));
        PDRA.setBounds(BTextX,Y+133,Tw-100,18);
        JTextField PDRD = new JTextField(PalServerConfig.getConfigValue("PalDamageRateDefense"));
        PDRD.setBounds(BTextX,Y+155,Tw-100,18);
        JTextField PDRAK = new JTextField(PalServerConfig.getConfigValue("PlayerDamageRateAttack"));
        PDRAK.setBounds(BTextX,Y+178,Tw-100,18);
        JTextField PDRDE = new JTextField(PalServerConfig.getConfigValue("PlayerDamageRateDefense"));
        PDRDE.setBounds(BTextX,Y+200,Tw-100,18);
        JTextField PSDR = new JTextField(PalServerConfig.getConfigValue("PlayerStaminaDecreaceRate"));
        PSDR.setBounds(BTextX,Y+223,Tw-100,18);
        JTextField PSADR = new JTextField(PalServerConfig.getConfigValue("PlayerStomachDecreaceRate"));
        PSADR.setBounds(BTextX,Y+245,Tw-100,18);
        JTextField PHPRR = new JTextField(PalServerConfig.getConfigValue("PlayerAutoHPRegeneRate"));
        PHPRR.setBounds(BTextX,Y+267,Tw-100,18);
        JTextField PAHRRS = new JTextField(PalServerConfig.getConfigValue("PlayerAutoHpRegeneRateInSleep"));
        PAHRRS.setBounds(BTextX,Y+290,Tw-100,18);
        JTextField PADRE = new JTextField(PalServerConfig.getConfigValue("PalStomachDecreaceRate"));
        PADRE.setBounds(BTextX,Y+312,Tw-100,18);
        JTextField PSDRE = new JTextField(PalServerConfig.getConfigValue("PalStaminaDecreaceRate"));
        PSDRE.setBounds(BTextX,Y+335,Tw-100,18);
        JTextField PLAHRR = new JTextField(PalServerConfig.getConfigValue("PalAutoHPRegeneRate"));
        PLAHRR.setBounds(BTextX,Y+357,Tw-100,18);

        JTextField PLAHRRS = new JTextField(PalServerConfig.getConfigValue("PalAutoHpRegeneRateInSleep"));
        PLAHRRS.setBounds(TBTextX,Y,Tw-100,18);
        JTextField BODR = new JTextField(PalServerConfig.getConfigValue("BuildObjectDamageRate"));
        BODR.setBounds(TBTextX,Y+23,Tw-100,18);
        JTextField BODDR = new JTextField(PalServerConfig.getConfigValue("BuildObjectDeteriorationDamageRate"));
        BODDR.setBounds(TBTextX,Y+45,Tw-100,18);
        JTextField CDR = new JTextField(PalServerConfig.getConfigValue("CollectionDropRate"));
        CDR.setBounds(TBTextX,Y+68,Tw-100,18);
        JTextField CODR = new JTextField(PalServerConfig.getConfigValue("CollectionObjectHpRate"));
        CODR.setBounds(TBTextX,Y+90,Tw-100,18);
        JTextField CORSR = new JTextField(PalServerConfig.getConfigValue("CollectionObjectRespawnSpeedRate"));
        CORSR.setBounds(TBTextX,Y+113,Tw-100,18);
        JTextField EDIR = new JTextField(PalServerConfig.getConfigValue("EnemyDropItemRate"));
        EDIR.setBounds(TBTextX,Y+135,Tw-100,18);

        JTextField DRMN = new JTextField(PalServerConfig.getConfigValue("DropItemMaxNum"));
        DRMN.setBounds(TBTextX,Y+315,Tw-100,18);
        JTextField DRMNU = new JTextField(PalServerConfig.getConfigValue("DropItemMaxNum_UNKO"));
        DRMNU.setBounds(TBTextX,Y+338,Tw-100,18);
        JTextField BCMN = new JTextField(PalServerConfig.getConfigValue("BaseCampMaxNum"));
        BCMN.setBounds(TBTextX,Y+360,Tw-100,18);
        JTextField BCWMN = new JTextField(PalServerConfig.getConfigValue("BaseCampWorkerMaxNum"));
        BCWMN.setBounds(VBTextX,Y,Tw-100,18);
        JTextField DIAMH = new JTextField(PalServerConfig.getConfigValue("DropItemAliveMaxHours"));
        DIAMH.setBounds(VBTextX,Y+22,Tw-100,18);
        JTextField ARGT = new JTextField(PalServerConfig.getConfigValue("AutoResetGuildTimeNoOnlinePlayers"));
        ARGT.setBounds(VBTextX,Y+67,Tw-100,18);
        JTextField GPMN = new JTextField(PalServerConfig.getConfigValue("GuildPlayerMaxNum"));
        GPMN.setBounds(VBTextX,Y+90,Tw-100,18);
        JTextField PEDH = new JTextField(PalServerConfig.getConfigValue("PalEggDefaultHatchingTime"));
        PEDH.setBounds(VBTextX,Y+112,Tw-100,18);
        JTextField WSR = new JTextField(PalServerConfig.getConfigValue("WorkSpeedRate"));
        WSR.setBounds(VBTextX,Y+135,Tw-100,18);//315+23
        JTextField CPMN = new JTextField(PalServerConfig.getConfigValue("CoopPlayerMaxNum"));
        CPMN.setBounds(VBTextX,Y+338,Tw-100,18);
        int LY = Y+400;

        JTextField SPMN = new JTextField(PalServerConfig.getConfigValue("ServerPlayerMaxNum"));
        SPMN.setBounds(BTextX-50,LY+2,Tw-100,18);
        JTextField SN = new JTextField(PalServerConfig.getConfigValue("ServerName"));
        SN.setBounds(BTextX-50,LY+25,Tw-100,18);
        JTextField SD = new JTextField(PalServerConfig.getConfigValue("ServerDescription"));
        SD.setBounds(BTextX-50,LY+47,Tw-100,18);

        JTextField AP = new JTextField(PalServerConfig.getConfigValue("AdminPassword"));
        AP.setBounds(TBTextX-160,LY+2,Tw-100,18);
        JTextField SP = new JTextField(PalServerConfig.getConfigValue("ServerPassword"));
        SP.setBounds(TBTextX-160,LY+25,Tw-100,18);
        JTextField PP = new JTextField(PalServerConfig.getConfigValue("PublicPort"));
        PP.setBounds(TBTextX-160,LY+47,Tw-100,18);

        var BEPTD = new JCheckBox("", Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnablePlayerToPlayerDamage")));
        BEPTD.setBounds(TBTextX,Y+180,Tw-100,18);
        var BEFF = new JCheckBox("", Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableFriendlyFire")));
        BEFF.setBounds(TBTextX,Y+200,Tw-100,18);
        var BEIE = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableInvaderEnemy")));
        BEIE.setBounds(TBTextX,Y+222,Tw-100,18);
        var BAUNKO = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bActiveUNKO")));
        BAUNKO.setBounds(TBTextX,Y+245,Tw-100,18);
        var BEAAP = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableAimAssistPad")));
        BEAAP.setBounds(TBTextX,Y+268,Tw-100,18);
        var BEAAK = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableAimAssistKeyboard")));
        BEAAK.setBounds(TBTextX,Y+291,Tw-100,18);//45
        var BRNOP = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bAutoResetGuildNoOnlinePlayers")));
        BRNOP.setBounds(VBTextX,Y+45,Tw-100,18);
        var BIM = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bIsMultiplay")));
        BIM.setBounds(VBTextX,Y+158,Tw-100,18);
        var BIP = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bIsPvP")));
        BIP.setBounds(VBTextX,Y+180,Tw-100,18);
        var BCPO = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bCanPickupOtherGuildDeathPenaltyDrop")));
        BCPO.setBounds(VBTextX,Y+202,Tw-100,18);
        var BENL = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableNonLoginPenalty")));
        BENL.setBounds(VBTextX,Y+225,Tw-100,18);
        var BEF = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableFastTravel")));
        BEF.setBounds(VBTextX,Y+247,Tw-100,18);
        var BSL = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bIsStartLocationSelectByMap")));
        BSL.setBounds(VBTextX,Y+269,Tw-100,18);
        var BEA = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bExistPlayerAfterLogout")));
        BEA.setBounds(VBTextX,Y+292,Tw-100,18);
        var BEDO = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bEnableDefenseOtherGuildPlayer")));
        BEDO.setBounds(VBTextX,Y+315,Tw-100,18);
        var RCONE = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("RCONEnabled")));

        RCONE.setBounds(VBTextX-280,LY+25,Tw-100,18);
        var BUA = new JCheckBox("",Boolean.parseBoolean(PalServerConfig.getConfigValue("bUseAuth")));
        BUA.setBounds(VBTextX-60,LY+25,Tw-100,18);
        page.add(BUA);
        page.add(RCONE);
        page.add(BEDO);
        page.add(BEA);
        page.add(BSL);
        page.add(BEF);
        page.add(BENL);
        page.add(BCPO);
        page.add(BIP);
        page.add(BIM);
        page.add(BRNOP);
        page.add(BEAAK);
        page.add(BEAAP);
        page.add(BAUNKO);
        page.add(BEFF);
        page.add(BEPTD);
        page.add(BEIE);
        submitButton.addActionListener(e -> {
            try {

                PalServerConfig.setConfigValue("bEnablePlayerToPlayerDamage",String.valueOf(BEPTD.isSelected()));
                PalServerConfig.setConfigValue("bEnableFriendlyFire",String.valueOf(BEFF.isSelected()));
                PalServerConfig.setConfigValue("bEnableInvaderEnemy",String.valueOf(BEIE.isSelected()));
                PalServerConfig.setConfigValue("bActiveUNKO",String.valueOf(BAUNKO.isSelected()));
                PalServerConfig.setConfigValue("bEnableAimAssistPad",String.valueOf(BEAAP.isSelected()));
                PalServerConfig.setConfigValue("bEnableAimAssistKeyboard",String.valueOf(BEAAK.isSelected()));
                PalServerConfig.setConfigValue("bAutoResetGuildNoOnlinePlayers",String.valueOf(BRNOP.isSelected()));
                PalServerConfig.setConfigValue("bIsMultiplay", String.valueOf(BIM.isSelected()));
                PalServerConfig.setConfigValue("bIsPvP", String.valueOf(BIP.isSelected()));
                PalServerConfig.setConfigValue("bCanPickupOtherGuildDeathPenaltyDrop",String.valueOf(BCPO.isSelected()));
                PalServerConfig.setConfigValue("bEnableNonLoginPenalty",String.valueOf(BENL.isSelected()));
                PalServerConfig.setConfigValue("bEnableFastTravel",String.valueOf(BEF.isSelected()));
                PalServerConfig.setConfigValue("bIsStartLocationSelectByMap", String.valueOf(BSL.isSelected()));
                PalServerConfig.setConfigValue("bExistPlayerAfterLogout", String.valueOf(BEA.isSelected()));
                PalServerConfig.setConfigValue("bEnableDefenseOtherGuildPlayer", String.valueOf(BEDO.isSelected()));
                PalServerConfig.setConfigValue("RCONEnabled",String.valueOf(RCONE.isSelected()));
                PalServerConfig.setConfigValue("bUseAuth",String.valueOf(BUA.isSelected()));

            } catch (NumberFormatException ex) {
                // 处理输入不是合法 double 的情况
                JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.ini.in.to.error"), I18nManager.getMessage("message.ini.in.to.error.title"), JOptionPane.ERROR_MESSAGE);
            }
        });

        JTextField PIP = new JTextField(PalServerConfig.getConfigValue("PublicIP"));
        PIP.setBounds(VBTextX-280,LY+2,Tw-100,18);
        JTextField RCONP = new JTextField(PalServerConfig.getConfigValue("RCONPort"));
        RCONP.setBounds(VBTextX-280,LY+47,Tw-100,18);

        JTextField RGI = new JTextField(PalServerConfig.getConfigValue("Region"));
        RGI.setBounds(VBTextX-60,LY+2,Tw-100,18);
        JTextField BIU = new JTextField(PalServerConfig.getConfigValue("BanListURL"));
        BIU.setBounds(VBTextX-60,LY+47,Tw-100,18);



        JLabel PathText = new JLabel(I18nManager.getMessage("message.ini.save.in"));
        PathText.setBounds(VBTextX-290,LY+78,Tw-100,18);



        submitButton.addActionListener(e -> {
            try {
                // 尝试将文本框中的文本转换为 double
                double DRSRV = Double.parseDouble(DRSR.getText());
                PalServerConfig.setConfigValue("DayTimeSpeedRate", String.valueOf(DRSRV));
                double NRSRV = Double.parseDouble(DRSR.getText());
                PalServerConfig.setConfigValue("NightTimeSpeedRate", String.valueOf(NRSRV));
                double ExpV = Double.parseDouble(EXPR.getText());
                PalServerConfig.setConfigValue("NightTimeSpeedRate", String.valueOf(ExpV));
                double PcrV = Double.parseDouble(PCR.getText());
                PalServerConfig.setConfigValue("PalCaptureRate",String.valueOf(PcrV));
                double PsnrV = Double.parseDouble(PSNR.getText());
                PalServerConfig.setConfigValue("PalSpawnNumRate",String.valueOf(PsnrV));
                double PdrV = Double.parseDouble(PDRA.getText());
                PalServerConfig.setConfigValue("PalDamageRateAttack",String.valueOf(PdrV));
                double PdrdV = Double.parseDouble(PDRD.getText());
                PalServerConfig.setConfigValue("PalDamageRateDefense", String.valueOf(PdrdV));
                double PdraKV = Double.parseDouble(PDRAK.getText());
                PalServerConfig.setConfigValue("PlayerDamageRateAttack", String.valueOf(PdraKV));
                double PdRdE = Double.parseDouble(PDRDE.getText());
                PalServerConfig.setConfigValue("PlayerDamageRateAttack",String.valueOf(PdRdE));
                double PsdR = Double.parseDouble(PSDR.getText());
                PalServerConfig.setConfigValue("PlayerStaminaDecreaceRate", String.valueOf(PsdR));
                double Psadr = Double.parseDouble(PSADR.getText());
                PalServerConfig.setConfigValue("PlayerStaminaDecreaceRate", String.valueOf(Psadr));
                double Phprr = Double.parseDouble(PHPRR.getText());
                PalServerConfig.setConfigValue("PlayerAutoHPRegeneRate",String.valueOf(Phprr));
                double Pahrrs = Double.parseDouble(PAHRRS.getText());
                PalServerConfig.setConfigValue("PlayerAutoHpRegeneRateInSleep",String.valueOf(Pahrrs));
                double Padre = Double.parseDouble(PADRE.getText());
                PalServerConfig.setConfigValue("PalStomachDecreaceRate", String.valueOf(Padre));
                double Psdre = Double.parseDouble(PSDRE.getText());
                PalServerConfig.setConfigValue("PalStaminaDecreaceRate", String.valueOf(Psdre));
                double Plahrr = Double.parseDouble(PLAHRR.getText());
                PalServerConfig.setConfigValue("PalAutoHPRegeneRate", String.valueOf(Plahrr));
                double Plahrrs = Double.parseDouble(PLAHRRS.getText());
                PalServerConfig.setConfigValue("PalAutoHpRegeneRateInSleep", String.valueOf(Plahrrs));
                double Bodr = Double.parseDouble(BODR.getText());
                PalServerConfig.setConfigValue("BuildObjectDamageRate", String.valueOf(Bodr));
                double Boddr = Double.parseDouble(BODDR.getText());
                PalServerConfig.setConfigValue("BuildObjectDeteriorationDamageRate", String.valueOf(Boddr));
                double Cdr = Double.parseDouble(CDR.getText());
                PalServerConfig.setConfigValue("CollectionDropRate", String.valueOf(Cdr));
                double Codr = Double.parseDouble(CODR.getText());
                PalServerConfig.setConfigValue("CollectionObjectHpRate", String.valueOf(Codr));
                double Corsr = Double.parseDouble(CORSR.getText());
                PalServerConfig.setConfigValue("CollectionObjectRespawnSpeedRate", String.valueOf(Corsr));
                double Edir = Double.parseDouble(EDIR.getText());
                PalServerConfig.setConfigValue("EnemyDropItemRate", String.valueOf(Edir));

                int Drnm = Integer.parseInt(DRMN.getText());
                PalServerConfig.setConfigValue("DropItemMaxNum", String.valueOf(Drnm));
                int Drnmu = Integer.parseInt(DRMNU.getText());
                PalServerConfig.setConfigValue("DropItemMaxNum_UNKO", String.valueOf(Drnmu));
                int Bcmn = Integer.parseInt(BCMN.getText());
                PalServerConfig.setConfigValue("BaseCampMaxNum", String.valueOf(Bcmn));
                int Bcwmn = Integer.parseInt(BCWMN.getText());
                PalServerConfig.setConfigValue("BaseCampWorkerMaxNum", String.valueOf(Bcwmn));

                double Diamh = Double.parseDouble(DIAMH.getText());
                PalServerConfig.setConfigValue("DropItemAliveMaxHours", String.valueOf(Diamh));

                double Argt = Double.parseDouble(ARGT.getText());
                PalServerConfig.setConfigValue("AutoResetGuildTimeNoOnlinePlayers", String.valueOf(Argt));
                int Gpmn = Integer.parseInt(GPMN.getText());
                PalServerConfig.setConfigValue("GuildPlayerMaxNum", String.valueOf(Gpmn));
                double Pedh = Double.parseDouble(PEDH.getText());
                PalServerConfig.setConfigValue("PalEggDefaultHatchingTime", String.valueOf(Pedh));
                double Wsr = Double.parseDouble(WSR.getText());
                PalServerConfig.setConfigValue("WorkSpeedRate", String.valueOf(Wsr));
                int Cpmn = Integer.parseInt(CPMN.getText());
                PalServerConfig.setConfigValue("CoopPlayerMaxNum", String.valueOf(Cpmn));
                int Spmn = Integer.parseInt(SPMN.getText());
                PalServerConfig.setConfigValue("ServerPlayerMaxNum", String.valueOf(Spmn));
                //PalServerConfig.setConfigValue("ServerName",strAdd(SN.getText()));
                PalServerConfig.setConfigValue("ServerName",strAdd(SN.getText()));
                PalServerConfig.setConfigValue("ServerDescription",strAdd(SD.getText()));
                PalServerConfig.setConfigValue("AdminPassword",strAdd(AP.getText()));
                PalServerConfig.setConfigValue("ServerPassword",strAdd(SP.getText()));
                int Pp = Integer.parseInt(PP.getText());
                PalServerConfig.setConfigValue("PublicPort", String.valueOf(Pp));
                PalServerConfig.setConfigValue("PublicIP",strAdd(PIP.getText()));
                int Rconp = Integer.parseInt(RCONP.getText());
                PalServerConfig.setConfigValue("RCONPort", String.valueOf(Rconp));
                PalServerConfig.setConfigValue("Region",strAdd(RGI.getText()));
                PalServerConfig.setConfigValue("BanListURL", strAdd(BIU.getText()));



                //IniExporter.testFor();
            } catch (NumberFormatException ex) {
                // 处理输入不是合法 double 的情况
                JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.ini.in.to.error"), I18nManager.getMessage("message.ini.in.to.error.title"), JOptionPane.ERROR_MESSAGE);
            }
        });
        page.add(PathText);
        page.add(BIU);
        page.add(RGI);
        page.add(RCONP);
        page.add(PIP);
        page.add(PP);
        page.add(SP);
        page.add(AP);
        page.add(SD);
        page.add(SN);
        page.add(SPMN);
        page.add(CPMN);
        page.add(WSR);
        page.add(PEDH);
        page.add(GPMN);
        page.add(ARGT);
        page.add(DIAMH);
        page.add(BCWMN);
        page.add(BCMN);
        page.add(DRMNU);
        page.add(DRMN);
        page.add(EDIR);
        page.add(CORSR);
        page.add(CODR);
        page.add(CDR);
        page.add(BODR);
        page.add(PLAHRRS);
        page.add(PLAHRR);
        page.add(PSDRE);
        page.add(PADRE);
        page.add(PAHRRS);
        page.add(PSADR);
        page.add(PSDR);
        page.add(PDRDE);
        page.add(PDRAK);
        page.add(PDRD);
        page.add(PDRA);
        page.add(PSNR);
        page.add(PCR);
        page.add(EXPR);
        page.add(DRSR);
        page.add(NTSR);
        page.add(PHPRR);
        page.add(BODDR);
    }
    private static final PTSConfig cfg = Config.loadConfig();
    private static String strAdd(String str) {
        return "\"" + str + "\"";
    }
    // TODO This addCheckBox

    private static void refreshPage(JButton x) {
        // 移除所有组件
        page.removeAll();
        page.invalidate();

        // 重新添加所有组件
        addPage();
        addText();
        sButton(x);

        // 刷新整个页面
        page.revalidate();
        page.repaint();
    }

    private static File chooseFile(JPanel parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(I18nManager.getMessage("message.backup.choose.ini.file"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showDialog(parentFrame, I18nManager.getMessage("message.backup.select"));

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected File: " + fileChooser.getSelectedFile());
            return fileChooser.getSelectedFile();
        } else {
            System.out.println("User canceled the operation");
        }
        return null;
    }
    private static void diffEnumSet(JList<DifficultyEnum> EList) {
        // 添加选择事件监听器
        EList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // 获取选定的值并打印到控制台
                List<DifficultyEnum> selectedItems = EList.getSelectedValuesList();
                for (DifficultyEnum item : selectedItems) {
                    PalServerConfig.setConfigValue("Difficulty", String.valueOf(item));
                }
            }
        });
    }
    private static void dpEnumSet(JList<DeathPenaltyEnum> DPList) {
        // 添加选择事件监听器
        DPList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // 获取选定的值并打印到控制台
                var selectedItems = DPList.getSelectedValuesList();
                for (DeathPenaltyEnum item : selectedItems) {
                    System.out.println(item);
                    String value = String.valueOf(item);
                    if(value.equals("ItemAndEqt")) value = "ItemAndEquipment";
                    PalServerConfig.setConfigValue("DeathPenalty", value);
                }
            }
        });
    }

}
