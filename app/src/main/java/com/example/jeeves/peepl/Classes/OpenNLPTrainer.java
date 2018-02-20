package com.example.jeeves.peepl.Classes;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Created by Jeeves on 12/19/2017.
 */

public class OpenNLPTrainer {

    private String trainedModelDirectoryName = "trained_models";
    private Context context;
    private String lang;
    private String symbol;
    private String trainingModelFileName;
    private String trainedModelFileName;
    private ArrayList<String> pictureText;
    private ContentEntityPair contentEntityPair;

    public OpenNLPTrainer(Context context, String lang, String symbol, String trainingModelFileName, String trainedModelFileName, ArrayList<String> pictureText)
    {
        this.context = context;
        this.lang = lang;
        this.symbol = symbol;
        this.trainingModelFileName = trainingModelFileName;
        this.trainedModelFileName = trainedModelFileName;
        this.pictureText = pictureText;
        this.contentEntityPair = new ContentEntityPair();
    }

    /*
     * Before using any of the other functions within the OpenNLPProcessor class, it is recommended to first use this function
     * to train your model on a training file, so that training does not occur while you are running your program. The other
     * functions will pull your trained model from an existing file, and will be able to classify data exponentially quicker.
     */

    public String trainModel() {

        AssetManager am = context.getAssets();
        Charset charset = Charset.forName("UTF-8");

        InputStreamFactory isf = new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return am.open(trainingModelFileName);
            }
        };

        try
        {
            ObjectStream<String> lineStream = new PlainTextByLineStream(isf, charset);
            ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
            TokenNameFinderFactory nameFinderFactory = new TokenNameFinderFactory();
            TokenNameFinderModel model;
            OutputStream modelOut = null;
            try
            {
                model = NameFinderME.train(lang, symbol, sampleStream, TrainingParameters.defaultParams(), nameFinderFactory);

                //TODO: getFilesDir() didn't work! Try checking with a test file to see what works, then retrain the model
                //TODO: and store it there!
                File trainedModelDirectory = new File(context.getFilesDir(), trainedModelDirectoryName);
                boolean created = trainedModelDirectory.mkdirs();
                File trainedModelFile = new File(trainedModelDirectory, trainedModelFileName);

                //boolean result = trainedModelFile.delete();

                if (!trainedModelFile.exists())
                {
                    boolean blah = trainedModelFile.createNewFile();
                }

                modelOut = new BufferedOutputStream(new FileOutputStream(trainedModelFile.toString()));

                if (model != null)
                {
                    model.serialize(modelOut);
                }

                return symbol + " model trained successfully";
            }

            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            finally
            {
                sampleStream.close();

                if (modelOut != null)
                {
                    modelOut.close();
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "Something goes wrong with training module.";
    }

}
