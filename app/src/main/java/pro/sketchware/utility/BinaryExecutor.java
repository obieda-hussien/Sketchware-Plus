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
        
        // Set working directory to a writable location
        File workingDir = new File("/data/local/tmp");
        if (workingDir.exists() && workingDir.canWrite()) {
            mProcess.directory(workingDir);
        }
        
        // Redirect error stream to capture all output
        mProcess.redirectErrorStream(false);
    }

    public String execute() {
        Process process = null;
        try {
            LogUtil.d(TAG, "Executing command: " + String.join(" ", mProcess.command()));
            
            // Verify the binary is executable before starting
            String binaryPath = mProcess.command().get(0);
            File binaryFile = new File(binaryPath);
            
            if (!binaryFile.exists()) {
                String error = "Binary does not exist: " + binaryPath;
                LogUtil.e(TAG, error);
                mWriter.append(error).append(System.lineSeparator());
                return mWriter.toString();
            }
            
            if (!binaryFile.canExecute()) {
                String error = "Binary is not executable: " + binaryPath + 
                              "\nPermissions: r=" + binaryFile.canRead() + 
                              ", w=" + binaryFile.canWrite() + 
                              ", x=" + binaryFile.canExecute();
                LogUtil.e(TAG, error);
                mWriter.append(error).append(System.lineSeparator());
                return mWriter.toString();
            }
            
            // Start the process
            process = mProcess.start();
            final Process finalProcess = process; // Make it final for lambda
            
            // Create threads to read stdout and stderr
            Thread errorReaderThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(finalProcess.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        mWriter.append(line).append(System.lineSeparator());
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "Error reading error stream", e);
                }
            });
            
            Thread outputReaderThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(finalProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        mOutputWriter.append(line).append(System.lineSeparator());
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "Error reading output stream", e);
                }
            });
            
            errorReaderThread.start();
            outputReaderThread.start();
            
            // Wait for process to complete with timeout
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                String timeoutError = "Process timed out after " + TIMEOUT_SECONDS + " seconds";
                LogUtil.e(TAG, timeoutError);
                mWriter.append(timeoutError).append(System.lineSeparator());
            } else {
                // Wait for reader threads to finish
                errorReaderThread.join(5000);
                outputReaderThread.join(5000);
                
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    String exitError = "Process exited with code: " + exitCode;
                    LogUtil.w(TAG, exitError);
                    mWriter.append(exitError).append(System.lineSeparator());
                }
                
                // Append stdout to the error writer for complete output
                String output = mOutputWriter.toString();
                if (!output.isEmpty()) {
                    mWriter.append("=== STDOUT ===").append(System.lineSeparator());
                    mWriter.append(output);
                }
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUtil.e(TAG, "Process execution interrupted", e);
            e.printStackTrace(new PrintWriter(mWriter));
        } catch (Exception e) {
            LogUtil.e(TAG, "Process execution failed", e);
            e.printStackTrace(new PrintWriter(mWriter));
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        
        return mWriter.toString();
    }

    public String getLog() {
        return mWriter.toString();
    }
    
    public String getOutput() {
        return mOutputWriter.toString();
    }
}