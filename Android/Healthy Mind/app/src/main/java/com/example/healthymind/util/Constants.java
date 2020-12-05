package com.example.healthymind.util;

public class Constants {
    public static final String TAG = "Healthy mind";

    public static final String FILE_NAME_PATTERN = "^[\\d]{14}_[_\\d]*\\..+$";

    public static final int STATE_INCOMING_NUMBER = 1;
    public static final int STATE_CALL_START = 2;
    public static final int STATE_CALL_END = 3;
    public static final int RECORDING_ENABLED = 4;
    public static final int RECORDING_DISABLED = 5;
    public static final int OUTGOING = 7;
    public static final String FIREBASE_CHILD_DEPRESSION_LEVELS = "patients";

    public static final String KEY_SCREEN = "key_screen";
    public static final String KEY_PHONE_NUMBER = "key_phone_number";
    public static final String DROID_LOGO = "M 193.71,67.17\n" +
            "           C 196.29,69.83 201.60,76.47 202.48,80.00\n" +
            "             203.56,84.40 200.63,87.70 196.09,86.67\n" +
            "             192.49,85.85 186.77,76.80 182.96,73.44\n" +
            "             173.06,64.71 161.28,59.71 148.00,60.01\n" +
            "             137.04,60.26 125.27,65.21 117.00,72.30\n" +
            "             113.05,75.68 106.71,84.85 102.96,85.67\n" +
            "             98.34,86.68 95.47,83.49 96.67,79.00\n" +
            "             97.69,75.19 102.26,69.82 105.04,67.04\n" +
            "             116.87,55.21 128.08,51.73 144.00,49.42\n" +
            "             161.34,47.32 181.60,54.63 193.71,67.17 Z\n" +
            "           M 176.99,78.90\n" +
            "           C 180.61,81.71 185.11,86.25 185.76,91.00\n" +
            "             186.15,93.92 185.27,97.06 181.96,97.58\n" +
            "             177.16,98.33 174.65,92.44 169.96,88.47\n" +
            "             164.42,83.76 155.40,79.62 148.00,80.18\n" +
            "             141.26,80.69 134.10,83.81 129.00,88.21\n" +
            "             125.32,91.39 121.56,97.95 116.11,96.67\n" +
            "             109.85,95.20 113.04,88.28 115.53,85.00\n" +
            "             121.86,76.67 130.06,72.67 140.00,70.46\n" +
            "             153.59,68.58 165.94,70.32 176.99,78.90 Z\n" +
            "           M 169.32,100.17\n" +
            "           C 171.97,103.25 176.52,110.78 171.69,113.98\n" +
            "             170.46,114.79 168.52,114.99 167.10,114.67\n" +
            "             163.80,113.91 162.54,110.49 160.47,108.18\n" +
            "             157.69,105.07 153.29,102.51 149.00,102.63\n" +
            "             144.46,102.75 140.26,105.76 137.37,109.04\n" +
            "             134.95,111.79 132.66,116.13 128.14,114.38\n" +
            "             124.41,112.94 124.78,108.05 126.01,105.00\n" +
            "             128.70,98.30 135.42,94.47 142.00,92.53\n" +
            "             151.99,90.90 162.34,92.08 169.32,100.17 Z\n" +
            "           M 168.20,135.00\n" +
            "           C 169.39,138.92 169.00,147.55 169.00,152.00\n" +
            "             169.00,152.00 169.00,184.00 169.00,184.00\n" +
            "             168.94,196.99 161.42,205.55 148.00,204.96\n" +
            "             136.03,204.43 130.05,195.14 130.00,184.00\n" +
            "             130.00,184.00 130.00,153.00 130.00,153.00\n" +
            "             130.00,141.15 128.03,129.51 141.00,123.41\n" +
            "             143.40,122.28 145.43,121.94 148.00,121.53\n" +
            "             157.13,121.31 165.45,125.87 168.20,135.00 Z\n" +
            "           M 140.98,228.30\n" +
            "           C 139.52,226.03 136.36,225.51 134.00,224.53\n" +
            "             130.77,223.18 126.73,220.68 124.01,218.48\n" +
            "             115.81,211.86 111.13,201.43 111.00,191.00\n" +
            "             110.96,187.44 110.45,176.80 112.02,174.15\n" +
            "             114.44,170.05 121.56,170.05 123.98,174.15\n" +
            "             125.40,176.54 124.97,182.11 125.00,185.00\n" +
            "             125.27,207.82 147.97,221.42 164.91,206.53\n" +
            "             174.06,198.49 173.80,190.90 174.00,180.00\n" +
            "             174.22,168.03 184.02,170.49 186.40,173.31\n" +
            "             188.75,176.11 188.01,185.21 188.00,189.00\n" +
            "             187.98,200.13 184.59,210.13 175.96,217.68\n" +
            "             173.12,220.17 168.50,223.07 165.00,224.53\n" +
            "             162.64,225.51 159.48,226.03 158.02,228.30\n" +
            "             156.79,230.22 157.01,233.75 157.00,236.00\n" +
            "             157.00,236.00 166.00,236.00 166.00,236.00\n" +
            "             166.00,236.00 166.00,251.00 166.00,251.00\n" +
            "             166.00,251.00 133.00,251.00 133.00,251.00\n" +
            "             133.00,251.00 133.00,236.00 133.00,236.00\n" +
            "             133.00,236.00 142.00,236.00 142.00,236.00\n" +
            "             141.99,233.75 142.21,230.22 140.98,228.30 Z";
}
