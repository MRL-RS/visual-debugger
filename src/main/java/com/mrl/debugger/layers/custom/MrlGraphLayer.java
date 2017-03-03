package com.mrl.debugger.layers.custom;

import com.mrl.debugger.Util;
import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseDtoLayer;
import com.mrl.debugger.remote.dto.EdgeDto;
import com.mrl.debugger.remote.dto.GraphDto;
import rescuecore2.misc.gui.ScreenTransform;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, tag = "graphLayer", caption = "Graph Layer", drawAllData = false)
public class MrlGraphLayer extends MrlBaseDtoLayer<GraphDto> {

    private static final Stroke STROKE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    @Override
    protected GraphDto transform(GraphDto graphDto, ScreenTransform t) {
        GraphDto transformed = new GraphDto();

        java.util.List<EdgeDto> transformedEdges = new ArrayList<>();
        for (EdgeDto edgeDto : graphDto.getEdgeDtoList()) {
            transformedEdges.add(transformEdge(edgeDto, t));
        }
        transformed.setEdgeDtoList(transformedEdges);

        return transformed;
    }


    protected EdgeDto transformEdge(EdgeDto edgeDto, ScreenTransform t) {

        EdgeDto transformed = new EdgeDto();
        transformed.setLine(Util.transform(edgeDto.getLine(), t));
        transformed.setPassable(edgeDto.isPassable());
        return transformed;
    }

    @Override
    protected void paintDto(GraphDto p, Graphics2D g) {
//        g.setColor(Color.YELLOW);
//        g.drawPolygon(p.getMyPartition().getPolygon());


        g.setStroke(STROKE);
        for (EdgeDto other : p.getEdgeDtoList()) {
            if (other.isPassable()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }
            g.draw(other.getLine());
        }
    }
}
