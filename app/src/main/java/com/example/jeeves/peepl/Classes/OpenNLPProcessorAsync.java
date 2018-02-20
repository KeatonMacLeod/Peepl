package com.example.jeeves.peepl.Classes;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 * Created by Jeeves on 11/4/2017.
 */

public class OpenNLPProcessorAsync extends AsyncTask<Void, Void, Void> {

    private String trainedModelDirectoryName = "trained_models";
    private Context context;
    private String lang;
    private String symbol;
    private String trainingModelFileName;
    private String trainedModelFileName;
    private ArrayList<String> pictureText;
    private ContentEntityPair contentEntityPair;


    public OpenNLPProcessorAsync(Context context, String lang, String symbol, String trainingModelFileName, String trainedModelFileName, ArrayList<String> pictureText)
    {
        this.context = context;
        this.lang = lang;
        this.symbol = symbol;
        this.trainingModelFileName = trainingModelFileName;
        this.trainedModelFileName = trainedModelFileName;
        this.pictureText = pictureText;
        this.contentEntityPair = new ContentEntityPair();
    }

    @Override
    protected Void doInBackground(Void... params) {
        findSports();
        return null;
    }

    private void findSports () {
        try
        {
            AssetManager am = context.getAssets();

            InputStream inputStreamTokenizer = am.open("en-token.zip");
            TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);
            PorterStemmer stemmer = new PorterStemmer();
            TokenizerME tokenizer = new TokenizerME(tokenModel);

            //TODO: You can keep developing on your Emulator however until you want to deal with this!
            //TODO: Testing on the Samsung Device is dying because it cannot find the model which is stored on the computer
            //TODO: You need to somehow store this in a place that the phone can access it, so that it can load the model without
            //TODO: throwing a FileNotFoundException! Try storing at "InternalStorage" as opposed to "ExternalStorage"
            File trainedModelDirectory = new File(context.getFilesDir(), trainedModelDirectoryName);
            File trainedModelFile = new File(trainedModelDirectory, "en-ner-sport.bin");
            InputStream inputStream = new FileInputStream(trainedModelFile);
            TokenNameFinderModel tokenNameFinderModel = new TokenNameFinderModel(inputStream);
            NameFinderME nameFinder = new NameFinderME(tokenNameFinderModel);

            for (int a = 0; a < pictureText.size(); a++) {
                String tokens[] = tokenizer.tokenize(pictureText.get(a));

                for (int b = 0; b < tokens.length; b++) {
                    tokens[b] = stemmer.stem(tokens[b]);
                }

                Span[] span = nameFinder.find(tokens);
                this.contentEntityPair.addContent(pictureText.get(a));
                this.contentEntityPair.addEntities(span);
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ContentEntityPair getContentEntityPair ()
    {
        return this.contentEntityPair;
    }

    // Find indices of entities that will always appear in a given format eg.) #a_hashtag or @an_at_symbol
    public Span[] find_deterministic_entity(String symbol, String symbol_name)
    {
        String text = "what's with #these homies #dissing my #girl? Why do they #gotta front, what did I #ever do to these #guys?";
        Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        List<Span> spans = new ArrayList<>();

        for (int i=0; i<tokens.length; i++)
        {
            if (tokens[i].charAt(0) == symbol.charAt(0))
                spans.add(new Span(i,i+1, symbol_name));
        }

        Span[] foundTags = spans.toArray(new Span[spans.size()]);

        return foundTags;

    }
}
