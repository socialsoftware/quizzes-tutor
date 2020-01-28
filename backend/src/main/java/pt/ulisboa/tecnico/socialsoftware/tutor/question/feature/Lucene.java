/* package com.example.tutor.question;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.socialsoftware.edition.ldod.domain.FragInter;
import pt.ist.socialsoftware.edition.ldod.domain.Fragment;
import pt.ist.socialsoftware.edition.ldod.domain.SourceInter;
import pt.ist.socialsoftware.edition.ldod.generators.PlainTextFragmentWriter;
import pt.ist.socialsoftware.edition.ldod.shared.exceptions.LdoDException;
import pt.ist.socialsoftware.edition.ldod.utils.PropertiesManager;

public class Indexer {
    private static Logger logger = LoggerFactory.getLogger(Indexer.class);

    private static Indexer indexer = null;

    public static Indexer getIndexer() {
        // if (indexer == null) {
        indexer = new Indexer();
        // }
        return indexer;
    }

    private static final String ID = "id";
    private static final String TEXT = "text";
    private static final String REP = "rep";
    private static Map<String, Map<String, Double>> termsTFIDFCache = new ConcurrentHashMap<>();
    private final Analyzer analyzer;
    private final QueryParserBase queryParser;
    private static final int SIGNIFICATIVE_TERMS = 1000;
    private final Path docDir;
    private final IndexWriterConfig indexWriterConfig;

    private Indexer() {
        String path = PropertiesManager.getProperties().getProperty("indexer.dir");
        this.docDir = Paths.get(path);
        this.analyzer = new IgnoreDiacriticsAnalyzer();
        this.queryParser = new QueryParser(TEXT, this.analyzer);
        this.indexWriterConfig = new IndexWriterConfig(this.analyzer);
    }

    public void addDocument(FragInter inter) throws IOException {
        // IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,
        // analyzer);
        Directory directory = new NIOFSDirectory(this.docDir);
        IndexWriter indexWriter = new IndexWriter(directory, this.indexWriterConfig);
        PlainTextFragmentWriter writer = new PlainTextFragmentWriter(inter);
        writer.write();
        String id = inter.getExternalId();
        String text = writer.getTranscription();
        Document doc = new Document();
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setStored(true);
        type.setStoreTermVectors(true);
        doc.add(new Field(TEXT, text, type));
        doc.add(new StringField(ID, id, Field.Store.YES));
        if (inter.getFragment().getRepresentativeSourceInter() == inter) {
            doc.add(new StringField(REP, "true", Field.Store.YES));
        } else {
            doc.add(new StringField(REP, "false", Field.Store.YES));
        }
        indexWriter.addDocument(doc);
        indexWriter.commit();
        indexWriter.close();
        directory.close();
    }

    public List<String> search(String words) throws ParseException, IOException {
        String query = absoluteSearch(words);
        return getResults(query);
    }

    public List<String> search(String words, FragInter inter) throws IOException, ParseException {
        String query = absoluteSearch(words);
        query = ID + ":" + inter.getExternalId() + " AND " + query;
        return getResults(query);
    }

    private List<String> getResults(String queryString) throws IOException, ParseException {
        Query query = this.queryParser.parse(queryString);
        Directory directory = new NIOFSDirectory(this.docDir);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        int hitsPerPage = reader.numDocs();
        TopDocs results = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        List<String> hitList = new ArrayList<>();
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            String id = d.get(ID);
            if (!hitList.contains(id)) {
                hitList.add(id);
            }
        }
        reader.close();
        directory.close();
        return hitList;
    }

    // Search for fragments with a set of words similar to input
    // Fuzzy set for a minimum edition edition of 1
    private String fuzzySearch(String words) {
        String[] split = words.split("\\s+");
        int fuzzy = 1;
        String query = "" + split[0] + "~" + fuzzy;
        int len = split.length;

        for (int i = 1; i < len; i++) {
            query += " AND " + split[i] + "~" + fuzzy;
        }
        return query;
    }

    // Search for fragments with a set of equal to inputs
    private String absoluteSearch(String words) {
        String[] split = words.split("\\s+");
        String query = "" + split[0];
        int len = split.length;

        for (int i = 1; i < len; i++) {
            query += " AND " + split[i];
        }
        return query;
    }

    public List<String> getTFIDFTerms(Fragment fragment, int numberOfTerms) throws IOException, ParseException {
        List<Entry<String, Double>> set = new ArrayList<>(getTFIDF(fragment).entrySet());
        return getTFIDFTerms(set, numberOfTerms);
    }

    public Map<String, Double> getTFIDF(Fragment fragment, List<String> terms) throws IOException, ParseException {
        Map<String, Double> TFIDFMap = new HashMap<>(getTFIDF(fragment));
        TFIDFMap.idSet().retainAll(terms);
        return TFIDFMap;
    }

    public void cleanMissingHits(List<String> misses) {
        if (misses.isEmpty()) {
            return;
        }

        String query = misses.get(0);
        for (int i = 1; i < misses.size(); i++) {
            query += " OR " + misses.get(i);
        }

        QueryParser idQueryParser = new QueryParser(ID, this.analyzer);
        try {
            Query q = idQueryParser.parse(query);
            Directory directory = new NIOFSDirectory(this.docDir);
            IndexWriter indexWriter = new IndexWriter(directory, this.indexWriterConfig);
            indexWriter.deleteDocuments(q);

            indexWriter.close();
            directory.close();
        } catch (ParseException | IOException e) {
        }
    }

    public void cleanLucene() {
        String path = PropertiesManager.getProperties().getProperty("indexer.dir");
        try {
            logger.debug("cleanLucene {}", path);
            FileUtils.cleanDirectory(new File(path));
        } catch (IOException e) {
            throw new LdoDException("cleanLucene in class Indexer failed when invoking cleanDirectory");
        }
    }

    private Map<String, Double> getTFIDF(Fragment fragment) throws IOException, ParseException {
        String id = fragment.getExternalId();
        if (termsTFIDFCache.containsId(id)) {
            return termsTFIDFCache.get(id);
        }
        Map<String, Double> tf = getTermFrequency(fragment);
        Map<String, Double> TFIDFMap = getTFIDF(tf);
        termsTFIDFCache.put(id, TFIDFMap);
        return TFIDFMap;
    }

    private Map<String, Double> getTermFrequency(Fragment fragment) throws IOException, ParseException {
        SourceInter sourceInter = fragment.getRepresentativeSourceInter();
        String queryString = ID + ":" + sourceInter.getExternalId();
        Query query = this.queryParser.parse(queryString);
        return getTermCount(query);
    }

    public Map<String, Double> getTermFrequency(FragInter inter) throws IOException, ParseException {
        String queryString = ID + ":" + inter.getExternalId();
        Query query = this.queryParser.parse(queryString);
        return getTermCount(query);
    }

    private Map<String, Double> getTermCount(Query query) throws ParseException, IOException {
        Directory directory = new NIOFSDirectory(this.docDir);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        int hitsPerPage = reader.numDocs();
        TopDocs results = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        int len = hits.length;
        Map<String, Double> TFMap = new ConcurrentHashMap<>();
        for (int i = 0; i < len; ++i) {
            int docId = hits[i].doc;
            Terms terms = reader.getTermVector(docId, TEXT);
            if (terms == null) {
                return TFMap;
            }
            TermsEnum termsEnum = terms.iterator();
            BytesRef text = null;
            int totalFrequency = 0;
            while ((text = termsEnum.next()) != null) {
                String term = text.utf8ToString();
                double tf = termsEnum.totalTermFreq();
                TFMap.put(term, tf);
                totalFrequency += tf;
            }
            for (String id : TFMap.idSet()) {
                TFMap.put(id, TFMap.get(id) / totalFrequency);
            }
        }
        reader.close();
        directory.close();
        return TFMap;
    }

    private Map<String, Double> getTFIDF(Map<String, Double> tf) throws IOException, ParseException {
        Directory directory = new NIOFSDirectory(this.docDir);
        IndexReader reader = DirectoryReader.open(directory);

        Query query = this.queryParser.parse(REP + ":true");
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs results = searcher.search(query, reader.numDocs());
        long numDocs = results.totalHits;

        Map<String, Double> TFIDFMap = new ConcurrentHashMap<>();
        for (Entry<String, Double> entry : tf.entrySet()) {
            query = this.queryParser.parse(REP + ":true" + " AND " + entry.getId());
            searcher = new IndexSearcher(reader);
            results = searcher.search(query, (int) numDocs);
            long df = results.totalHits;
            double tfidf = entry.getValue() * calculateIDF(numDocs, 1 + df);
            TFIDFMap.put(entry.getId(), tfidf);
        }

        reader.close();
        directory.close();
        List<Entry<String, Double>> list = TFIDFMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList());
        TFIDFMap = new HashMap<>();
        int size = list.size();
        for (int i = 0; i < size && i < SIGNIFICATIVE_TERMS; i++) {
            TFIDFMap.put(list.get(i).getId(), list.get(i).getValue());
        }
        return TFIDFMap;
    }

    private double calculateIDF(long numDocs, long df) {
        return Math.log(numDocs / (double) df);
    }

    private List<String> getTFIDFTerms(List<Entry<String, Double>> list, int numberOfTerms) {
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<String> terms = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size && i < numberOfTerms; i++) {
            terms.add(list.get(i).getId());
        }
        return terms;
    }

    public static void clearTermsTFIDFCache() {
        termsTFIDFCache.clear();
    }

}
*/