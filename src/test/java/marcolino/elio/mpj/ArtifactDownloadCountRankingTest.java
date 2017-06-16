package marcolino.elio.mpj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marcolino.elio.mpj.integration.RestClient;
import marcolino.elio.mpj.integration.RestClientException;
import marcolino.elio.mpj.integration.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.integration.artifactory.model.Artifact;
import marcolino.elio.mpj.utils.ArtifactDownloadCountRanking;
import marcolino.elio.mpj.utils.Constants;
import marcolino.elio.mpj.worker.GetMostPopularArtifactsWorker;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

public class ArtifactDownloadCountRankingTest {

    private static Artifact mockedArtifact;

    @BeforeClass
    public static void initMock() throws RestClientException {
        mockedArtifact = mock(Artifact.class);
    }

    @Test
    public void testUpdateRankingSingleElement() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);

        assertEquals(1, ranking.getRanking().size());
        assertNotNull(ranking.getRanking().get(0));

    }

    @Test
    public void testUpdateRankingSecondElementSmaller() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);

        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(2, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(1, ranking.getRanking().get(1).getDownloadCount().intValue());
    }

    @Test
    public void testUpdateRankingSecondElementBigger() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);

        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 3);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(3, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(2, ranking.getRanking().get(1).getDownloadCount().intValue());
    }

    @Test
    public void testUpdateRankingThridElementSmaller() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 3);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);
        // Third Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(3, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(2, ranking.getRanking().get(1).getDownloadCount().intValue());
    }

    @Test
    public void testUpdateRankingThridElementBigger() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);
        // Third Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 3);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(3, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(2, ranking.getRanking().get(1).getDownloadCount().intValue());
    }

    @Test
    public void testUpdateRankingSecondElementEqual() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(1, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(1, ranking.getRanking().get(1).getDownloadCount().intValue());
    }

    @Test
    public void testUpdateRankingThirdElementEqualTop() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);
        // Third element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(2, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(2, ranking.getRanking().get(1).getDownloadCount().intValue());
    }
    
    @Test
    public void testUpdateRankingThirdElementEqualBottom() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(2);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);
        // Third element
        newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);

        assertEquals(2, ranking.getRanking().size());
        assertEquals(2, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(1, ranking.getRanking().get(1).getDownloadCount().intValue());
    }
    
    @Test
    public void testUpdateRankingShiftElementsFromTop() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(3);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 3);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);
        // Third element
        newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);
        // Forth element
        newResult = new ArtifactDownloadCount(mockedArtifact, 4);
        ranking.updateRanking(newResult);

        assertEquals(3, ranking.getRanking().size());
        assertEquals(4, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(3, ranking.getRanking().get(1).getDownloadCount().intValue());
        assertEquals(2, ranking.getRanking().get(2).getDownloadCount().intValue());
    }
    
    @Test
    public void testUpdateRankingShiftElementsFromMiddle() throws Exception {

        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(3);
        // First Element
        ArtifactDownloadCount newResult = new ArtifactDownloadCount(mockedArtifact, 4);
        ranking.updateRanking(newResult);
        // Second Element
        newResult = new ArtifactDownloadCount(mockedArtifact, 2);
        ranking.updateRanking(newResult);
        // Third element
        newResult = new ArtifactDownloadCount(mockedArtifact, 1);
        ranking.updateRanking(newResult);
        // Forth element
        newResult = new ArtifactDownloadCount(mockedArtifact, 3);
        ranking.updateRanking(newResult);

        assertEquals(3, ranking.getRanking().size());
        assertEquals(4, ranking.getRanking().get(0).getDownloadCount().intValue());
        assertEquals(3, ranking.getRanking().get(1).getDownloadCount().intValue());
        assertEquals(2, ranking.getRanking().get(2).getDownloadCount().intValue());
    }

}
