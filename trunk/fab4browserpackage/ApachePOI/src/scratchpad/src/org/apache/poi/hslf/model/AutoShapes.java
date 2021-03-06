/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hslf.model;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.hslf.model.autoshape.AutoShapeDefinition;
import org.apache.poi.hslf.model.autoshape.AutoShapeParser;

/**
 * Stores definition of auto-shapes.
 * See the Office Drawing 97-2007 Binary Format Specification for details.
 *
 * TODO: follow the spec and define all the auto-shapes
 *
 * @author Yegor Kozlov
 */
public final class AutoShapes {
    protected static ShapeOutline[] shapes;
    protected static List<AutoShapeDefinition> as ;

    /**
     * Return shape outline by shape type
     * @param type shape type see {@link ShapeTypes}
     *
     * @return the shape outline
     */
    public static ShapeOutline getShapeOutline(int type){


        ShapeOutline outline = shapes[type];
        return outline;
    }

    /**
     * Auto-shapes are defined in the [0,21600] coordinate system.
     * We need to transform it into normal slide coordinates
     *
     */
    public static java.awt.Shape transform(java.awt.Shape outline, Rectangle2D anchor){
        AffineTransform at = new AffineTransform();
        at.translate(anchor.getX(), anchor.getY());
        at.scale(
                1.0f/21600*anchor.getWidth(),
                1.0f/21600*anchor.getHeight()
        );
        return at.createTransformedShape(outline);
    }

    static {
        shapes = new ShapeOutline[255];

        //        shapes[ShapeTypes.Rectangle] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                Rectangle2D path = new Rectangle2D.Float(0, 0, 21600, 21600);
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.RoundRectangle] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //                RoundRectangle2D path = new RoundRectangle2D.Float(0, 0, 21600, 21600, adjval, adjval);
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Ellipse] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                Ellipse2D path = new Ellipse2D.Float(0, 0, 21600, 21600);
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Diamond] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(10800, 0);
        //                path.lineTo(21600, 10800);
        //                path.lineTo(10800, 21600);
        //                path.lineTo(0, 10800);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        //m@0,l,21600r21600
        //        shapes[ShapeTypes.IsocelesTriangle] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 10800);
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(adjval, 0);
        //                path.lineTo(0, 21600);
        //                path.lineTo(21600, 21600);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.RightTriangle] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(0, 0);
        //                path.lineTo(21600, 21600);
        //                path.lineTo(0, 21600);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Parallelogram] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(adjval, 0);
        //                path.lineTo(21600, 0);
        //                path.lineTo(21600 - adjval, 21600);
        //                path.lineTo(0, 21600);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Trapezoid] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(0, 0);
        //                path.lineTo(adjval, 21600);
        //                path.lineTo(21600 - adjval, 21600);
        //                path.lineTo(21600, 0);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Hexagon] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(adjval, 0);
        //                path.lineTo(21600 - adjval, 0);
        //                path.lineTo(21600, 10800);
        //                path.lineTo(21600 - adjval, 21600);
        //                path.lineTo(adjval, 21600);
        //                path.lineTo(0, 10800);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Octagon] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 6326);
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(adjval, 0);
        //                path.lineTo(21600 - adjval, 0);
        //                path.lineTo(21600, adjval);
        //                path.lineTo(21600, 21600-adjval);
        //                path.lineTo(21600-adjval, 21600);
        //                path.lineTo(adjval, 21600);
        //                path.lineTo(0, 21600-adjval);
        //                path.lineTo(0, adjval);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //                shapes[ShapeTypes.Plus] = new ShapeOutline(){
        //                    public java.awt.Shape getOutline(Shape shape){
        //                        int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //        
        //                        GeneralPath path = new GeneralPath();
        //                        path.moveTo(adjval, 0);
        //                        path.lineTo(21600 - adjval, 0);
        //                        path.lineTo(21600 - adjval, adjval);
        //                        path.lineTo(21600, adjval);
        //                        path.lineTo(21600, 21600-adjval);
        //                        path.lineTo(21600-adjval, 21600-adjval);
        //                        path.lineTo(21600-adjval, 21600);
        //                        path.lineTo(adjval, 21600);
        //                        path.lineTo(adjval, 21600-adjval);
        //                        path.lineTo(0, 21600-adjval);
        //                        path.lineTo(0, adjval);
        //                        path.lineTo(adjval, adjval);
        //                        path.closePath();
        //                        return path;
        //                    }
        //                };
        //
        //        shapes[ShapeTypes.Pentagon] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(10800, 0);
        //                path.lineTo(21600, 8259);
        //                path.lineTo(21600 - 4200, 21600);
        //                path.lineTo(4200, 21600);
        //                path.lineTo(0, 8259);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.DownArrow] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m0@0 l@1@0 @1,0 @2,0 @2@0,21600@0,10800,21600xe
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 16200);
        //                int adjval2 = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(0, adjval);
        //                path.lineTo(adjval2, adjval);
        //                path.lineTo(adjval2, 0);
        //                path.lineTo(21600-adjval2, 0);
        //                path.lineTo(21600-adjval2, adjval);
        //                path.lineTo(21600, adjval);
        //                path.lineTo(10800, 21600);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.UpArrow] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m0@0 l@1@0 @1,21600@2,21600@2@0,21600@0,10800,xe
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //                int adjval2 = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(0, adjval);
        //                path.lineTo(adjval2, adjval);
        //                path.lineTo(adjval2, 21600);
        //                path.lineTo(21600-adjval2, 21600);
        //                path.lineTo(21600-adjval2, adjval);
        //                path.lineTo(21600, adjval);
        //                path.lineTo(10800, 0);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Arrow] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m@0, l@0@1 ,0@1,0@2@0@2@0,21600,21600,10800xe
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 16200);
        //                int adjval2 = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(adjval, 0);
        //                path.lineTo(adjval, adjval2);
        //                path.lineTo(0, adjval2);
        //                path.lineTo(0, 21600-adjval2);
        //                path.lineTo(adjval, 21600-adjval2);
        //                path.lineTo(adjval, 21600);
        //                path.lineTo(21600, 10800);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.LeftArrow] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m@0, l@0@1,21600@1,21600@2@0@2@0,21600,,10800xe
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //                int adjval2 = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(adjval, 0);
        //                path.lineTo(adjval, adjval2);
        //                path.lineTo(21600, adjval2);
        //                path.lineTo(21600, 21600-adjval2);
        //                path.lineTo(adjval, 21600-adjval2);
        //                path.lineTo(adjval, 21600);
        //                path.lineTo(0, 10800);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.Can] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m10800,qx0@1l0@2qy10800,21600,21600@2l21600@1qy10800,xem0@1qy10800@0,21600@1nfe
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
        //
        //                GeneralPath path = new GeneralPath();
        //
        //                path.append(new Arc2D.Float(0, 0, 21600, adjval, 0, 180, Arc2D.OPEN), false);
        //                path.moveTo(0, adjval/2);
        //
        //                path.lineTo(0, 21600 - adjval/2);
        //                path.closePath();
        //
        //                path.append(new Arc2D.Float(0, 21600 - adjval, 21600, adjval, 180, 180, Arc2D.OPEN), false);
        //                path.moveTo(21600, 21600 - adjval/2);
        //
        //                path.lineTo(21600, adjval/2);
        //                path.append(new Arc2D.Float(0, 0, 21600, adjval, 180, 180, Arc2D.OPEN), false);
        //                path.moveTo(0, adjval/2);
        //                path.closePath();
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.LeftBrace] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m21600,qx10800@0l10800@2qy0@11,10800@3l10800@1qy21600,21600e
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 1800);
        //                int adjval2 = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUST2VALUE, 10800);
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(21600, 0);
        //
        //                path.append(new Arc2D.Float(10800, 0, 21600, adjval*2, 90, 90, Arc2D.OPEN), false);
        //                path.moveTo(10800, adjval);
        //
        //                path.lineTo(10800, adjval2 - adjval);
        //
        //                path.append(new Arc2D.Float(-10800, adjval2 - 2*adjval, 21600, adjval*2, 270, 90, Arc2D.OPEN), false);
        //                path.moveTo(0, adjval2);
        //
        //                path.append(new Arc2D.Float(-10800, adjval2, 21600, adjval*2, 0, 90, Arc2D.OPEN), false);
        //                path.moveTo(10800, adjval2 + adjval);
        //
        //                path.lineTo(10800, 21600 - adjval);
        //
        //                path.append(new Arc2D.Float(10800, 21600 - 2*adjval, 21600, adjval*2, 180, 90, Arc2D.OPEN), false);
        //
        //                return path;
        //            }
        //        };
        //
        //        shapes[ShapeTypes.RightBrace] = new ShapeOutline(){
        //            public java.awt.Shape getOutline(Shape shape){
        //                //m,qx10800@0 l10800@2qy21600@11,10800@3l10800@1qy,21600e
        //                int adjval = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUSTVALUE, 1800);
        //                int adjval2 = shape.getEscherProperty(EscherProperties.GEOMETRY__ADJUST2VALUE, 10800);
        //
        //                GeneralPath path = new GeneralPath();
        //                path.moveTo(0, 0);
        //
        //                path.append(new Arc2D.Float(-10800, 0, 21600, adjval*2, 0, 90, Arc2D.OPEN), false);
        //                path.moveTo(10800, adjval);
        //
        //                path.lineTo(10800, adjval2 - adjval);
        //
        //                path.append(new Arc2D.Float(10800, adjval2 - 2*adjval, 21600, adjval*2, 180, 90, Arc2D.OPEN), false);
        //                path.moveTo(21600, adjval2);
        //
        //                path.append(new Arc2D.Float(10800, adjval2, 21600, adjval*2, 90, 90, Arc2D.OPEN), false);
        //                path.moveTo(10800, adjval2 + adjval);
        //
        //                path.lineTo(10800, 21600 - adjval);
        //
        //                path.append(new Arc2D.Float(-10800, 21600 - 2*adjval, 21600, adjval*2, 270, 90, Arc2D.OPEN), false);
        //
        //                return path;
        //            }
        //        };


        shapes[ShapeTypes.StraightConnector1] = new ShapeOutline(){
            public java.awt.Shape getOutline(Shape shape){
                Path2D p = new Path2D.Float();
                p.moveTo(0, 0);
                p.lineTo( 21600, 21600);

                float width = (float) SimpleShape.DEFAULT_LINE_WIDTH;
                if (shape instanceof SimpleShape) {

                    SimpleShape r = (SimpleShape) shape;
                    width = (float) r.getLineWidth();
                }

                Rectangle anchor = shape.getAnchor();
                p.setWindingRule(p.WIND_NON_ZERO);

//                if (shape.getEscherProperty(EscherProperties.LINESTYLE__LINEENDARROWHEAD)>0)
//                    p.append(createArrowShape(new Point(
//                            0, 0) , new Point(21600, 21600), width), false);
//                if (shape.getEscherProperty(EscherProperties.LINESTYLE__LINESTARTARROWHEAD)>0)
//                    p.append(createArrowShape(new Point(
//                            21600, 21600) , new Point(0, 0), width), false);

                return p;
            }
        };
        shapes[ShapeTypes.BentConnector3] = new ShapeOutline(){
            public java.awt.Shape getOutline(Shape shape){
                Path2D p = new Path2D.Float();
                int k = shape
                .getEscherProperty(
                        (short) (EscherProperties.GEOMETRY__ADJUSTVALUE ), 10750);
                p.moveTo(0, 0);
                p.lineTo(k, 0);
                p.lineTo( k, 21600);

                p.lineTo( 21600, 21600);
                //                Rectangle anchor = shape.getAnchor();
                //                
                //                float width = (float) SimpleShape.DEFAULT_LINE_WIDTH;
                //                if (shape instanceof SimpleShape) {
                //
                //                    SimpleShape r = (SimpleShape) shape;
                //                    width = (float) r.getLineWidth();
                //                }
                //
                //                if (shape.getEscherProperty(EscherProperties.LINESTYLE__LINEENDARROWHEAD)>0)
                //                    p.append(drawArrowFlat(
                //                            (int) anchor.getX()+  k,  (int) anchor.getY() +21600, (int) anchor.getX()+   21600,  (int) anchor.getX()+  21600 , width,
                //                           5, 5), false);
                //                if (shape.getEscherProperty(EscherProperties.LINESTYLE__LINESTARTARROWHEAD)>0)
                //                    p.append(drawArrowFlat(
                //                            0, 0,  k,
                //                             0,width,
                //                            5, 5), false);
                //                  
                //                
                return p;
            }
        };




        shapes[ShapeTypes.CurvedConnector3] = new ShapeOutline(){
            public java.awt.Shape getOutline(Shape shape){
                Path2D p = new Path2D.Float();
                int k = shape
                .getEscherProperty(
                        (short) (EscherProperties.GEOMETRY__ADJUSTVALUE ), 10750);
                p.moveTo(0, 0);
                p.quadTo(k, 0,k, 21600/2);
                p.quadTo(k, 21600, 21600,21600);
                return p;
            }
        };
        try {
            as = AutoShapeParser.parseShapes(new InputStreamReader(AutoShapeParser.class.getResourceAsStream("/shapeDescriptions.txt")));
            for (AutoShapeDefinition a: as) {
                if (a.path==null){
                    continue;
                }
                //                if (a.name.equals("Plus"))
                //                    continue;
                ShapeOutline s = AutoShapeParser.parseShapeData(a.path, a.guideFormulas, a.adjustmentValues);
                Integer n = (Integer) ShapeTypes.typesR.get(a.name);
                if (n == null) {
                    n = (Integer) ShapeTypes.typesR.get(a.internalName);
                }
                if (n!=null) {
                    if (shapes[n]==null) {
                        shapes[n] = s; 
                    }
                }


            }
        } catch (Exception e){}
    }
    private static int yCor(int len, double dir) {
        return (int) (len * Math.cos(dir));
    }

    private static int xCor(int len, double dir) {
        return (int) (len * Math.sin(dir));
    }


    public static final Polygon drawArrowFlat (int xCenter, int yCenter,
            int x, int y, float stroke, int i1, int i2) {
        double aDir = Math.atan2(xCenter - x, yCenter - y);

        Polygon tmpPoly = new Polygon();
        if (i1 == 0) {
            i1 = 8 + (int) (stroke);
            i2 = 6 + (int) stroke;
        } else {
            i1 *= stroke + 1;
            i2 *= stroke + 1;
        }
        tmpPoly.addPoint(x, y);
        tmpPoly.addPoint(x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
        //  tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
        tmpPoly.addPoint(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5));
        tmpPoly.addPoint(x, y);
        return tmpPoly;
    }

    public static java.awt.Shape createArrowShape(Point fromPt, Point toPt, float width) {
        Polygon arrowPolygon = new Polygon();
        arrowPolygon.addPoint(3,1);
        arrowPolygon.addPoint(3,3);
        arrowPolygon.addPoint(6,0);
        arrowPolygon.addPoint(3,1);



        Point midPoint = midpoint(fromPt, toPt);

        double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

        AffineTransform transform = new AffineTransform();
        transform.translate(midPoint.x, midPoint.y);
        double ptDistance = fromPt.distance(toPt);
        double scale = ptDistance / 12.0; // 12 because it's the length of the arrow polygon.
        transform.scale(scale, scale);
        transform.rotate(rotate);

        return transform.createTransformedShape(arrowPolygon);
    }

    private static Point midpoint(Point p1, Point p2) {
        return new Point((int)((p1.x + p2.x)/2.0), 
                (int)((p1.y + p2.y)/2.0));
    }
}