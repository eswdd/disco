/*
 * Copyright 2015, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.exemel.disco.modules.zipkin.impl;

import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinData;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinDataBuilder;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinRequestUUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ZipkinRequestUUIDImplTest {

    private ZipkinRequestUUID victim;

    @Mock
    private RequestUUID discoUuid;

    @Mock
    private ZipkinData zipkinData;

    @Mock
    private ZipkinDataBuilder zipkinDataBuilder;

    @Mock
    private RequestUUID requestUUID;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void ZipkinRequestUUIDImpl_WhenDiscoIdIsNull_ShouldThrowNPE() {
        victim = new ZipkinRequestUUIDImpl(null, null);
    }

    @Test
    public void getRootUUIDComponent_ShouldDeferToDiscoUUID() {
        String rootUUIDComponent = "abcde-1234-fghij-5678-klmno";

        when(discoUuid.getRootUUIDComponent()).thenReturn(rootUUIDComponent);

        victim = new ZipkinRequestUUIDImpl(discoUuid, null);

        assertEquals(rootUUIDComponent, victim.getRootUUIDComponent());
    }

    @Test
    public void getParentUUIDComponent_ShouldDeferToDiscoUUID() {
        String parentUUIDComponent = "abcde-1234-fghij-5678-klmno";

        when(discoUuid.getParentUUIDComponent()).thenReturn(parentUUIDComponent);

        victim = new ZipkinRequestUUIDImpl(discoUuid, null);

        assertEquals(parentUUIDComponent, victim.getParentUUIDComponent());
    }

    @Test
    public void getLocalUUIDComponent_ShouldDeferToDiscoUUID() {
        String localUUIDComponent = "abcde-1234-fghij-5678-klmno";

        when(discoUuid.getLocalUUIDComponent()).thenReturn(localUUIDComponent);

        victim = new ZipkinRequestUUIDImpl(discoUuid, null);

        assertEquals(localUUIDComponent, victim.getLocalUUIDComponent());
    }

    @Test
    public void toDiscoLogString_ShouldDeferToDiscoUUID() {
        String discoLogString = "abcde-1234-fghij-5678-klmno";

        when(discoUuid.toDiscoLogString()).thenReturn(discoLogString);

        victim = new ZipkinRequestUUIDImpl(discoUuid, null);

        assertEquals(discoLogString, victim.toDiscoLogString());
    }

    @Test
    public void getUUID_ShouldDeferToDiscoGetUUID() {
        String discoUUID = "abcde-1234-fghij-5678-klmno";

        when(discoUuid.getUUID()).thenReturn(discoUUID);

        victim = new ZipkinRequestUUIDImpl(discoUuid, null);

        assertEquals(discoUUID, victim.getUUID());
    }

    @Test
    public void isZipkinTracingEnabled_WhenZipkinDataBuilderIsNull_ShouldReturnFalse() {
        victim = new ZipkinRequestUUIDImpl(discoUuid);

        assertFalse(victim.isZipkinTracingEnabled());
    }

    @Test
    public void isZipkinTracingEnabled_WhenZipkinDataBuilderIsNotNull_ShouldReturnTrue() {
        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        assertTrue(victim.isZipkinTracingEnabled());
    }

    @Test(expected = NullPointerException.class)
    public void buildZipkinData_WhenSpanNameIsNull_ShouldThrowNPE() {
        victim = new ZipkinRequestUUIDImpl(discoUuid, null);

        victim.buildZipkinData(null);
    }

    @Test
    public void buildZipkinData_WhenCalledForTheFirstTime_ShouldPopulateAndReturnZipkinData() {
        String spanName = "Span Name";

        when(zipkinDataBuilder.spanName(spanName)).thenReturn(zipkinDataBuilder);
        when(zipkinDataBuilder.build()).thenReturn(zipkinData);

        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        ZipkinData result = victim.buildZipkinData(spanName);

        assertEquals(zipkinData, result);
    }

    @Test(expected = IllegalStateException.class)
    public void buildZipkinData_WhenZipkinDataAlreadyExists_ShouldThrowISE() {
        String spanName = "Span Name";

        when(zipkinDataBuilder.spanName(spanName)).thenReturn(zipkinDataBuilder);
        when(zipkinDataBuilder.build()).thenReturn(zipkinData);

        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        // Build Zipkin data for the first time
        victim.buildZipkinData(spanName);

        // Attempt to build Zipkin data again
        victim.buildZipkinData(spanName);
    }

    @Test
    public void isZipkinTracingReady_WhenZipkinDataHasNotBeenBuilt_ShouldReturnFalse() {
        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        assertFalse(victim.isZipkinTracingReady());
    }

    @Test
    public void isZipkinTracingReady_WhenZipkinDataHasBeenBuilt_ShouldReturnTrue() {
        String spanName = "Span Name";

        when(zipkinDataBuilder.spanName(spanName)).thenReturn(zipkinDataBuilder);
        when(zipkinDataBuilder.build()).thenReturn(zipkinData);

        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        victim.buildZipkinData(spanName);

        assertTrue(victim.isZipkinTracingReady());
    }

    @Test
    public void toString_ShouldConcatenateZipkinDataWithDiscoUUID() {
        String zipkinSpanName = "Span Name";
        String discoUUID = "abcde-1234-fghij-5678-klmno";
        String zipkinDataToString = "ZipkinDataImpl{spanName=" + zipkinSpanName + "}";
        String expectedResult = "ZipkinRequestUUIDImpl{discoUuid=" + discoUUID +
                ", zipkinData=" + zipkinDataToString + "}";

        when(zipkinDataBuilder.spanName(zipkinSpanName)).thenReturn(zipkinDataBuilder);
        when(zipkinDataBuilder.build()).thenReturn(zipkinData);
        when(discoUuid.getUUID()).thenReturn(discoUUID);
        when(zipkinData.toString()).thenReturn(zipkinDataToString);

        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        victim.buildZipkinData(zipkinSpanName);
        String result = victim.toString();

        assertEquals(expectedResult, result);
    }

    @Test
    public void getZipkinData_WhenZipkinDataHasBeenBuilt_ShouldReturnBuiltData() {
        String spanName = "Span Name";

        when(zipkinDataBuilder.spanName(spanName)).thenReturn(zipkinDataBuilder);
        when(zipkinDataBuilder.build()).thenReturn(zipkinData);

        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        victim.buildZipkinData(spanName);

        assertEquals(victim.getZipkinData(), zipkinData);
    }

    @Test(expected = IllegalStateException.class)
    public void getZipkinData_WhenZipkinDataHasNotBeenBuiltYet_ShouldThrowISE() {
        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);

        victim.getZipkinData();
    }

    @Test(expected = IllegalStateException.class)
    public void getZipkinData_WhenZipkinIsDisabledForThisRequest_ShouldThrowISE() {
        victim = new ZipkinRequestUUIDImpl(discoUuid);

        victim.getZipkinData();
    }

    @Test
    public void getNewSubUUID_WhenZipkinTracingIsNotEnabled_ChildShouldNotBeTracedEither() {

        when(discoUuid.getNewSubUUID()).thenReturn(requestUUID);

        victim = new ZipkinRequestUUIDImpl(discoUuid);

        RequestUUID result = victim.getNewSubUUID();

        assertNotNull(result);
        assertFalse(((ZipkinRequestUUID) result).isZipkinTracingEnabled());
    }

    @Test
    public void getNewSubUUID_WhenZipkinTracingIsEnabled_ShouldReturnTraceableChild() {
        String spanName = "Span Name";
        long traceId = 123456789;
        long spanId = 987654321;
        short port = 9101;

        when(zipkinDataBuilder.spanName(spanName)).thenReturn(zipkinDataBuilder);
        when(zipkinDataBuilder.build()).thenReturn(zipkinData);
        when(discoUuid.getNewSubUUID()).thenReturn(requestUUID);
        when(zipkinData.getTraceId()).thenReturn(traceId);
        when(zipkinData.getSpanId()).thenReturn(spanId);
        when(zipkinData.getPort()).thenReturn(port);

        victim = new ZipkinRequestUUIDImpl(discoUuid, zipkinDataBuilder);
        victim.buildZipkinData(spanName);

        RequestUUID result = victim.getNewSubUUID();

        assertNotNull(result);
        assertTrue(((ZipkinRequestUUID) result).isZipkinTracingEnabled());
    }
}
