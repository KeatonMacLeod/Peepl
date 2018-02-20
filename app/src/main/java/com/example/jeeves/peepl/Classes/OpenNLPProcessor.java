package com.example.jeeves.peepl.Classes;

import android.content.Context;
import android.content.res.AssetManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

/**
 * Created by Jeeves on 11/4/2017.
 */

public class OpenNLPProcessor implements Runnable{

    private String trainedModelDirectoryName = "trained_models";
    private final Context context;
    private final String lang;
    private final String symbol;
    private final String trainingModelFileName;
    private final String trainedModelFileName;
    private final ArrayList<String> pictureText;
    private final ContentEntityPair contentEntityPair;


    public OpenNLPProcessor(Context context, String lang, String symbol, String trainingModelFileName, String trainedModelFileName, ArrayList<String> pictureText)
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
    public void run() {
         findSports();
    }

    public void tagPartsOfSpeech(Context context)
    {
        try
        {
            AssetManager am = context.getAssets();

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(am.open("test.xml"));
            //Loading Parts of speech-maxent model
            InputStream inputStream = am.open("en-pos-maxent.zip");
            POSModel model = new POSModel(inputStream);

            //Creating an object of WhitespaceTokenizer class
            WhitespaceTokenizer whitespaceTokenizer= WhitespaceTokenizer.INSTANCE;

            //Tokenizing the sentence
            String sentence = "Hi welcome to Tutorialspoint";
            String[] tokens = whitespaceTokenizer.tokenize(sentence);

            //Instantiating POSTaggerME class
            POSTaggerME tagger = new POSTaggerME(model);

            //Generating tags
            String[] tags = tagger.tag(tokens);

            //Instantiating POSSample class
            POSSample sample = new POSSample(tokens, tags);
        }
        catch (IOException  | ParserConfigurationException | SAXException e)
        {
            e.printStackTrace();
        }
    }

    private void findSports () {
        try
        {
            AssetManager am = context.getAssets();

            InputStream inputStreamTokenizer = am.open("en-token.zip");
            TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);
            PorterStemmer stemmer = new PorterStemmer();
            TokenizerME tokenizer = new TokenizerME(tokenModel);

            File trainedModelDirectory = new File(context.getExternalCacheDir(), trainedModelDirectoryName);
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
