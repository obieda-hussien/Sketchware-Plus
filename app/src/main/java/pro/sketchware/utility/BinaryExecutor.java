package pro.sketchware.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import mod.jbk.util.LogUtil;

public class BinaryExecutor {
    private static final String TAG = "BinaryExecutor";
    private static final int TIMEOUT_SECONDS = 300; // 5 minutes timeout
    
    private final ProcessBuilder mProcess = new ProcessBuilder();
    private final StringWriter mWriter = new StringWriter();
    private final StringWriter mOutputWriter = new StringWriter();

    public void setCommands(ArrayList<String> arrayList) {
        mProcess.command(arrayList);
        
        // Set working directory to avoid path issues
        File binaryFile = new File(arrayList.get(0));
        if (binaryFile.exists() && binaryFile.getParentFile() != null) {
            mProcess.directory(binaryFile.getParentFile());
        }
        
        // Merge error stream with output for better debugging
        mProcess.redirectErrorStream(false);
    }

    public String execute() {
        Process process = null;
        try {
            // Validate binary exists and is executable
            String binaryPath = mProcess.command().get(0);
            File binaryFile = new File(binaryPath);
            
            if (!binaryFile.exists()) {
                String error = "Binary does not exist: " + binaryPath;
                LogUtil.e(TAG, error);
                mWriter.append(error).append(System.lineSeparator());
                return mWriter.toString();
            }
            
            if (!binaryFile.canExecute()) {
                String error = "Binary is not executable: " + binaryPath + "\n" +
                              "Attempting to fix permissions...";
                LogUtil.e(TAG, error);
                mWriter.append(error).append(System.lineSeparator());
                
                // Try to fix permissions on-the-fly
                boolean fixed = attemptPermissionFix(binaryFile);
                if (!fixed) {
                    String permError = "Failed to fix permissions. Please restart the build.";
                    mWriter.append(permError).append(System.lineSeparator());
                    return mWriter.toString();
                }
            }
            
            LogUtil.d(TAG, "Executing command: " + String.join(" ", mProcess.command()));
            
            // Start the process
            process = mProcess.start();
            
            // Read both stdout and stderr
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
            );
            BufferedReader outputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            // Read error stream
            String line;
            while ((line = errorReader.readLine()) != null) {
                mWriter.append(line).append(System.lineSeparator());
            }
            
            // Read output stream
            while ((line = outputReader.readLine()) != null) {
                mOutputWriter.append(line).append(System.lineSeparator());
            }
            
            // Wait for process to complete with timeout
            boolean completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                String timeoutError = "Process timed out after " + TIMEOUT_SECONDS + " seconds";
                LogUtil.e(TAG, timeoutError);
                mWriter.append(timeoutError).append(System.lineSeparator());
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String exitError = "Process exited with code: " + exitCode;
                LogUtil.e(TAG, exitError);
                mWriter.append(exitError).append(System.lineSeparator());
            }
            
            errorReader.close();
            outputReader.close();
            
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception during binary execution", e);
            e.printStackTrace(new PrintWriter(mWriter));
            
            // Add helpful context
            mWriter.append(System.lineSeparator());
            mWriter.append("Command: ").append(String.join(" ", mProcess.command()));
            mWriter.append(System.lineSeparator());
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        
        return mWriter.toString();
    }
    
    /**
     * Attempts to fix file permissions using multiple strategies
     */
    private boolean attemptPermissionFix(File binaryFile) {
        try {
            // Try setExecutable first
            if (binaryFile.setExecutable(true, false)) {
                LogUtil.d(TAG, "Fixed permissions using setExecutable");
                return binaryFile.canExecute();
            }
            
            // Try chmod via Runtime
            Process chmodProcess = Runtime.getRuntime().exec(
                new String[]{"chmod", "755", binaryFile.getAbsolutePath()}
            );
            int exitCode = chmodProcess.waitFor();
            
            if (exitCode == 0 && binaryFile.canExecute()) {
                LogUtil.d(TAG, "Fixed permissions using chmod command");
                return true;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to fix permissions", e);
        }
        
        return false;
    }

    public String getLog() {
        return mWriter.toString();
    }
    
    public String getOutput() {
        return mOutputWriter.toString();
    }
}