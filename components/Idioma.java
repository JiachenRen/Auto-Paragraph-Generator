package components;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;

/**
 * Created by Jiachen on 2/26/18.
 * Manages SimpleNLG, CoreNLP, and WordNet functions
 */
public class Idioma {
    static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);
    private static Dictionary dict;

    static {
        try {
            dict = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }


    public static String conjugate(WordElement word, Tense tense) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.TENSE, tense);
        return realiser.realise(inflected).getRealisation();
    }

    public static String conjugate(WordElement word, Form form) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.FORM, form);
        return realiser.realise(inflected).getRealisation();
    }

    public static String infinitiveFormOf(POS pos, String word) {
        try {
            IndexWord inf = dict.lookupIndexWord(pos, word);
            if (inf != null) {
                return inf.getLemma();
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static VerbTense getVerbTense(String verb) {
        try {
            verb = verb.toLowerCase();
            IndexWord indexWord = dict.lookupIndexWord(POS.VERB, verb);
            if (indexWord == null) return null;
            if (indexWord.getLemma().equals(verb)) return VerbTense.INFINITIVE;
            WordElement word = lexicon.getWord(indexWord.getLemma(), LexicalCategory.VERB);
            if (conjugate(word, Tense.PRESENT).equals(verb)) {
                return VerbTense.THIRD_PERSON_SINGULAR;
            } else if (conjugate(word, Tense.PAST).equals(verb)) {
                return VerbTense.PAST;
            } else if (conjugate(word, Form.PAST_PARTICIPLE).equals(verb)) {
                return VerbTense.PAST_PARTICIPLE;
            } else if (conjugate(word, Form.PRESENT_PARTICIPLE).equals(verb)) {
                return VerbTense.GERUND;
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean is(POS pos, String candidate) {
        try {
            return dict.lookupIndexWord(pos, candidate) != null;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<String> getSynonyms(POS pos, String word) throws JWNLException {
        ArrayList<String> synonyms = new ArrayList<>();
        IndexWord indexWord = dict.lookupIndexWord(pos, word);
        if (indexWord == null) return synonyms;
        String inf = Idioma.infinitiveFormOf(pos, word);
        for (long i : indexWord.getSynsetOffsets()) {
            Synset set = dict.getSynsetAt(pos, i);
            for (Word word1 : set.getWords()) {
                String lemma = word1.getLemma();
                if (lemma.equals(inf))
                    continue;
                if (!synonyms.contains(lemma) && !lemma.contains(inf)) {
                    synonyms.add(lemma);
                }
            }
        }
        return synonyms;
    }
}