package model;

public class PlantRecognition {
    private int id;
    private String imageName;
    private String imagePath;
    private String recognitionResult;
    private double confidence;
    private String recognizeTime;

    public PlantRecognition() {}

    public PlantRecognition(int id, String imageName, String imagePath, String recognitionResult, double confidence, String recognizeTime) {
        this.id = id;
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.recognitionResult = recognitionResult;
        this.confidence = confidence;
        this.recognizeTime = recognizeTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getRecognitionResult() { return recognitionResult; }
    public void setRecognitionResult(String recognitionResult) { this.recognitionResult = recognitionResult; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getRecognizeTime() { return recognizeTime; }
    public void setRecognizeTime(String recognizeTime) { this.recognizeTime = recognizeTime; }
}