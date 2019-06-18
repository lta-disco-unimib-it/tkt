/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;

/**
 *
 * @author AleX
 */
public class PathProject {
    //cartella progetto principale
    private File folderProject;
    //cartella Timed Automata
    private File folderTA;
    //cartella tracce per inferire TA
    private File folderTrace;
    //cartella tracce da validare
    private File folderTraceValidation;
    //cartella contente i risultati di validazione
    private File folderResult;
    //stringa contente il separatore di sistema
    private String separator;
    
    public PathProject(){
        folderProject = null;
        folderTA = null;
        folderTrace = null;
        folderTraceValidation = null;
        folderResult = null;
        separator = System.getProperty("file.separator");
    }
    
    public void setFolderProject(File filePath){
        this.folderProject = filePath;
    }
    public void setFolderTA(String path){
        this.folderTA = new File(path);
    }
    public void setFolderTrace(String path){
        this.folderTrace = new File(path);
    }
    public void setFolderTraceValidation(String path){
        this.folderTraceValidation = new File(path);
    }
    public void setFolderResult(String path){
        this.folderResult = new File(path);
    }
    
    public File getFolderProject(){
        return this.folderProject;
    }
    public File getFolderTA(){
        return this.folderTA;
    }
    public File getFolderTrace(){
        return this.folderTrace;
    }
    public File getFolderTraceValidation(){
        return this.folderTraceValidation;
    }
    public File getFolderResult(){
        return this.folderResult;
    }
    public String getSeparator(){
        return separator;
    }
    public boolean checkFolderTa(){
        if (folderTA.listFiles().length != 0){
            return true;
        }else{
            return false;
    }
    }
    public boolean checkFolderTrace(){
        if (folderTrace.listFiles().length != 0){
            return true;
        }else{
            return false;
        }
    }
    public boolean checkFolderValidation(){
        if (folderTraceValidation.listFiles().length != 0){
            return true;
        }else{
            return false;
        }
    }
    public boolean checkFolderResult(){
        if (folderResult.listFiles().length != 0){
            return true;
        }else{
            return false;
        }
    }
    
    
}
