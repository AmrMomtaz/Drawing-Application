<template>
  <div id="app">
    <div id="modifyShapes">
      <p>Modify Shapes</p>
      <div id="modifications">
        <button style="width:100%" v-on:click="changeStrokeColorToSelected();">change stroke color to selected</button>
        <button style="width:100%" v-on:click="changeFillColorToSelected();">change fill color to selected</button>
        <button style="width:100%" v-on:click="changeLineWidthToSelected();">change line width to selected</button>
        <button style="width:50%" v-on:click="copyShape();">copy</button>
        <button style="width:50%" v-on:click="remove();">delete</button>
      </div>
      <p>Drawn Shapes</p>
    <div id="ShapesHistory">
      <button v-on:click="selectShapeToModify(shape.id);" v-for="shape in ShapesList" :key="shape.id">{{shape.type}}</button>
    </div>
    </div>
    <div id="toolsPanel">
      <div id="save-load-new">
      <button v-on:click="save();">Save</button>
      <button v-on:click="load();">Load</button>
      <button v-on:click="undoRedo('undo');">Undo</button>
      <button v-on:click="undoRedo('redo');">Redo</button>
      </div>
      <p>Shapes</p>
      <div id="selectShapePanel">
      <button v-on:click="selectShape('square')">square</button>
      <button v-on:click="selectShape('rectangle')">rectangle</button>
      <button v-on:click="selectShape('circle')">circle</button>
      <button v-on:click="selectShape('ellipse')">ellipse</button>
      <button v-on:click="selectShape('triangle')">triangle</button>
      <button v-on:click="selectShape('line')">line</button>
      <button class="span-two" v-on:click="selectShape(null)">Deselect</button>
      </div>
      <p style="font-size:11px">Fill Color / Stroke Color</p>
      <div id="color-Pickers">
      <v-swatches v-model="fillStyle"></v-swatches>
      <v-swatches v-model="strokeStyle"></v-swatches>
      </div>
      <p> change line width </p>
      <p style="background-color:#d9d9d9"> {{linewidth}} px </p>
      <div id="lineWidthResize">
      <button v-on:click="resizeLine(1);">+</button>
      <button v-on:click="resizeLine(-1);">-</button>
      </div>
      <p>saved files</p>
      <div id="load">
      <button style="height:30px" v-on:click="loadPaint(fileName.id);" v-for="fileName in loadFiles" :key="fileName.id">{{fileName.name}}</button>
      </div>
    </div>
    <div id="PaintPanel">
      <canvas id="MainCanvas" width="800" height="500"/>
        <canvas id="GhostCanvas" width="800" height="500" @mousedown="MymouseDown" @mouseup="MymouseUp" @mousemove="createMoveModify"  />
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import VSwatches from 'vue-swatches'
import 'vue-swatches/dist/vue-swatches.css'
export default {
  
  name: 'App',
  components: { VSwatches },
  mounted(){
  this.canvas = document.getElementById("MainCanvas");
  this.ctx = this.canvas.getContext("2d");
  this.HEIGHT=this.canvas.height;
  this.WIDTH=this.canvas.width;
  this.ghostCanvas = document.getElementById("GhostCanvas");
  this.ghostCtx= this.ghostCanvas.getContext("2d");
  /*to make the colorPicked label have the same background color as the fillStyle(selected color) */
  },

  data() {
      return {
        loadFiles:[],
        /*selection details */
        linewidth:2,
        fillStyle:'#1CA085',
        strokeStyle:'#222F3D',
        /*data saved and sent to backend */
        id:0,
        width:0,
        height:0,
        lenght:0,
        raduis:0,
        raduisX:0,
        raduisY:0,
        x:0,
        y:0,
        x1:0,
        x2:0,
        x3:0,
        y1:0,
        y2:0,
        y3:0,
        /*Canvas intialisation */
        canvas:null,
        ctx:null,
        HEIGHT:0,
        WIDTH:0,
        ghostCanvas:null,
        ghostCtx:null,
        /*selections Handles */
        ShapesList:[],
        ShapeInGhost:null,
        selectionsList:[],
        selectedShape:null,
        OnAScalingRectangle:null,
        selectedScalingRectangle:null,
        myDown:false,
        mouseX:0,
        mouseY:0,
        DiffrenceX:0,
        DiffrenceY:0,
        SelectedForMove:false,
        SelectedForResize:false,
        moving:false,
        scaling:false,
        creating:false,
      };
  },
  methods:{
    save(){
      var Name = prompt("Enter a name for your paint")
      if(Name!="" && Name!=null){
        var ext = prompt("Enter the file extension : (type : \"xml\" or \"json\" or \"XML\" or \"JSON\")");
        if (ext != null){
          ext = ext.toString().toLowerCase();
        }
        if (ext == "xml" || ext == "json"){
          axios.post("http://localhost:8080/save"+Name+ext);
          alert("Your paint has been saved");
        }else{
          alert("You have entered wrong input !");
        }
      }
      else{
        alert("you have to give your file a name");
      }
    },
    loadPaint(path){
      axios.get("http://localhost:8080/load"+path).then(resp=>{
        this.ShapesList=[];
        this.ShapesList=resp.data
        this.clearAll();
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT)
        this.repaint();
      })
    },
    load(){
      axios.get("http://localhost:8080/get_list").then(resp=>{
        this.loadFiles=resp.data;
      })
    },
    copyShape(){
      if(isNaN(this.selectedShape)||this.selectedShape==null){return;}
      axios.get("http://localhost:8080/copy"+this.selectedShape).then(resp=>{
        let shape=resp.data;
        this.ShapesList.splice(0,0,shape);
        this.selectShapeToModify(shape.id);

      })
    },
    undoRedo(operation){
      this.clearLoadList();
      let shape;
      if(this.ShapeInGhost!=null){
          this.copyGhostToMain();
      }
      axios.get("http://localhost:8080/"+operation).then(resp=>{
      if(resp.data=="")return;
      console.log(resp.data)
      shape=resp.data;
      for(let i =0 ; i<this.ShapesList.length;i++){
        if(this.ShapesList[i].id==shape.id){
          if(shape.type==null){
            this.ShapesList.splice(i,1);
          }
          else if(
              this.ShapesList[i].type==shape.type &&
              this.ShapesList[i].linewidth==shape.linewidth &&
              this.ShapesList[i].fillStyle==shape.fillStyle &&
              this.ShapesList[i].strokeStyle==shape.strokeStyle &&
              this.ShapesList[i].id==shape.id &&
              this.ShapesList[i].width==shape.width &&
              this.ShapesList[i].height==shape.height &&
              this.ShapesList[i].raduis==shape.raduis &&
              this.ShapesList[i].raduisX==shape.raduisX &&
              this.ShapesList[i].raduisY==shape.raduisY &&
              this.ShapesList[i].x==shape.x &&
              this.ShapesList[i].y==shape.y &&
              this.ShapesList[i].x1==shape.x1 &&
              this.ShapesList[i].x2==shape.x2 &&
              this.ShapesList[i].x3==shape.x3 &&
              this.ShapesList[i].y1==shape.y1 &&
              this.ShapesList[i].y2==shape.y2 &&
              this.ShapesList[i].y3==shape.y3 
          ){
            this.undoRedo(operation); 
          }
          else{
          this.ShapesList.splice(i,1);
          this.ShapesList.push(shape);
          }
          this.ctx.clearRect(0,0,this.WIDTH,this.HEIGHT);
          this.repaint();
          return;
        }
      }
        if(shape.type!=null){
        this.ShapesList.push(shape);
        this.ctx.clearRect(0,0,this.WIDTH,this.HEIGHT);
        this.repaint();
        }
      })
    },
    remove(){
      this.clearLoadList();
      if(this.ShapeInGhost!=null){
      axios.delete("http://localhost:8080/"+this.ShapeInGhost.id);
      this.ShapeInGhost=null;
      this.selectionsList=[];
      this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT);
      this.selectedShape=null;
      }
    },
    changeStrokeColorToSelected(){
      this.clearLoadList();
      if(this.ShapeInGhost!=null){ 
        this.ShapeInGhost.strokeStyle=this.strokeStyle;
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT)
        this.DrawComponent("ghostCtx",this.ShapeInGhost);
        this.drawSelectionFrame(this.ShapeInGhost); 
        axios.post('http://localhost:8080/modify',{
        type:this.ShapeInGhost.type,
        linewidth:this.ShapeInGhost.linewidth,
        fillStyle:this.ShapeInGhost.fillStyle,
        strokeStyle:this.ShapeInGhost.strokeStyle,
        id:this.ShapeInGhost.id,
        width:this.ShapeInGhost.width,
        height:this.ShapeInGhost.height,
        raduis:this.ShapeInGhost.raduis,
        raduisX:this.ShapeInGhost.raduisX,
        raduisY:this.ShapeInGhost.raduisY,
        x:this.ShapeInGhost.x,
        y:this.ShapeInGhost.y,
        x1:this.ShapeInGhost.x1,
        x2:this.ShapeInGhost.x2,
        x3:this.ShapeInGhost.x3,
        y1:this.ShapeInGhost.y1,
        y2:this.ShapeInGhost.y2,
        y3:this.ShapeInGhost.y3,
      })
      }
    },
    changeFillColorToSelected(){
      this.clearLoadList();
      if(this.ShapeInGhost!=null){
        this.ShapeInGhost.fillStyle=this.fillStyle;
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT)
        this.DrawComponent("ghostCtx",this.ShapeInGhost);
        this.drawSelectionFrame(this.ShapeInGhost); 
      axios.post('http://localhost:8080/modify',{
        type:this.ShapeInGhost.type,
        linewidth:this.ShapeInGhost.linewidth,
        fillStyle:this.ShapeInGhost.fillStyle,
        strokeStyle:this.ShapeInGhost.strokeStyle,
        id:this.ShapeInGhost.id,
        width:this.ShapeInGhost.width,
        height:this.ShapeInGhost.height,
        raduis:this.ShapeInGhost.raduis,
        raduisX:this.ShapeInGhost.raduisX,
        raduisY:this.ShapeInGhost.raduisY,
        x:this.ShapeInGhost.x,
        y:this.ShapeInGhost.y,
        x1:this.ShapeInGhost.x1,
        x2:this.ShapeInGhost.x2,
        x3:this.ShapeInGhost.x3,
        y1:this.ShapeInGhost.y1,
        y2:this.ShapeInGhost.y2,
        y3:this.ShapeInGhost.y3,
      })
      }
      
    },
    changeLineWidthToSelected(){
      this.clearLoadList();
      if(this.ShapeInGhost!=null){
        this.ShapeInGhost.linewidth=this.linewidth;
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT)
        this.DrawComponent("ghostCtx",this.ShapeInGhost);
        this.drawSelectionFrame(this.ShapeInGhost); 
      axios.post('http://localhost:8080/modify',{
        type:this.ShapeInGhost.type,
        linewidth:this.ShapeInGhost.linewidth,
        fillStyle:this.ShapeInGhost.fillStyle,
        strokeStyle:this.ShapeInGhost.strokeStyle,
        id:this.ShapeInGhost.id,
        width:this.ShapeInGhost.width,
        height:this.ShapeInGhost.height,
        raduis:this.ShapeInGhost.raduis,
        raduisX:this.ShapeInGhost.raduisX,
        raduisY:this.ShapeInGhost.raduisY,
        x:this.ShapeInGhost.x,
        y:this.ShapeInGhost.y,
        x1:this.ShapeInGhost.x1,
        x2:this.ShapeInGhost.x2,
        x3:this.ShapeInGhost.x3,
        y1:this.ShapeInGhost.y1,
        y2:this.ShapeInGhost.y2,
        y3:this.ShapeInGhost.y3,
      })
      }
    },
    selectShape(shapeName){
        this.clearLoadList();
        this.selectedShape=shapeName;
        if(this.ShapeInGhost!=null){
          this.copyGhostToMain();
      }
    } ,   
    selectShapeToModify(id){
      this.clearLoadList();
      this.selectedShape=id;
      if(this.ShapeInGhost!=null){
        this.copyGhostToMain();
      }
      for(let i =0 ; i<this.ShapesList.length ; i++){
        if(this.ShapesList[i].id==id){
          this.ShapeInGhost=this.ShapesList[i];
          this.setProperties(this.ShapeInGhost.linewidth,this.ShapeInGhost.strokeStyle,this.ShapeInGhost.fillStyle);
          this.DrawComponent("ghostCtx",this.ShapeInGhost);
          this.drawSelectionFrame(this.ShapesList[i]);
          this.deleteShapeFromMain();
          this.setProperties(this.linewidth,this.strokeStyle,this.fillStyle);
          break;
        }
      }
    },
    clearLoadList(){
      if(this.loadFiles!=[]){
        this.loadFiles=[];
      }
    },
    Sign(value){
      if(value>=0)return 1;
      else if(value<0)return -1;
    },
    resizeLine(resizeBy){
      if(resizeBy==1 && this.linewidth<50){
        this.linewidth+=1;
      }
      else if(resizeBy==-1 && this.linewidth>1){
        this.linewidth-=1;
      }
    },
    isInsideABox(e){
      if(((this.selectionsList[0].x>this.selectionsList[1].x && e.offsetX>this.selectionsList[1].x
      && e.offsetX<this.selectionsList[0].x) || (this.selectionsList[0].x<this.selectionsList[1].x && e.offsetX<this.selectionsList[1].x 
      && e.offsetX>this.selectionsList[0].x))){
        if(this.ShapeInGhost.type=="line"){
          if(((this.selectionsList[0].y>this.selectionsList[1].y && e.offsetY>this.selectionsList[1].y 
      && e.offsetY<this.selectionsList[0].y) || (this.selectionsList[0].y<this.selectionsList[1].y && e.offsetY<this.selectionsList[1].y 
      && e.offsetY>this.selectionsList[0].y))){
        return true;
        }
      }
      else if(((this.selectionsList[1].y>this.selectionsList[2].y && e.offsetY>this.selectionsList[2].y 
      && e.offsetY<this.selectionsList[1].y) || (this.selectionsList[1].y<this.selectionsList[2].y && e.offsetY<this.selectionsList[2].y 
      && e.offsetY>this.selectionsList[1].y))){
        return true;
      }
      }
      return false;
      
    },
    selectionHandle(e){
      this.OnAScalingRectangle=null;
      this.SelectedForResize=false;
      this.SelectedForMove=false;
      document.body.style.cursor="default"
      if(this.selectionsList.length>0){
        for(let i =0 ;i<this.selectionsList.length;i++){
        if(e.offsetX<this.selectionsList[i].x+5 && e.offsetX>this.selectionsList[i].x-5 && e.offsetY<this.selectionsList[i].y+5 && e.offsetY>this.selectionsList[i].y-5){
          document.body.style.cursor="pointer"
          this.SelectedForResize=true;
          this.OnAScalingRectangle=i;
        }
        }
        if(this.isInsideABox(e) && this.SelectedForResize==false){
          document.body.style.cursor="grab"
          this.SelectedForMove=true;
          
        }
      }
    },
    AddSelectionBoxes(shape){
        if(shape.type=="square" || shape.type=="rectangle"){
          this.ghostCtx.strokeRect( shape.x-shape.linewidth/2*this.Sign(shape.width), shape.y-shape.linewidth/2*this.Sign(shape.height), shape.width+shape.linewidth*this.Sign(shape.width), shape.height+shape.linewidth*this.Sign(shape.height));
          this.selectionsList.push({
            x:shape.x-shape.linewidth/2*this.Sign(shape.width),
            y:shape.y-shape.linewidth/2*this.Sign(shape.height),
          })
          this.selectionsList.push({
            x:shape.x+shape.width+shape.linewidth/2*this.Sign(shape.width),
            y:shape.y-shape.linewidth/2*this.Sign(shape.height),
          })
          this.selectionsList.push({
            x:shape.x+shape.width+shape.linewidth/2*this.Sign(shape.width),
            y:shape.y+shape.height+shape.linewidth/2*this.Sign(shape.height),
          })
          this.selectionsList.push({
            x:shape.x-shape.linewidth/2*this.Sign(shape.width),
            y:shape.y+shape.height+shape.linewidth/2*this.Sign(shape.height),
          })
      }
      else if(shape.type=="circle"){
        this.ghostCtx.strokeRect(shape.x-shape.linewidth/2-shape.raduis, shape.y-shape.linewidth/2-shape.raduis,2*(shape.raduis)+shape.linewidth,2*(shape.raduis)+shape.linewidth);
        this.selectionsList.push({
          x:shape.x-shape.raduis-shape.linewidth/2,
          y:shape.y-shape.raduis-shape.linewidth/2,
        })
        this.selectionsList.push({
          x:shape.x+shape.raduis+shape.linewidth/2,
          y:shape.y-shape.raduis-shape.linewidth/2,
        })
        this.selectionsList.push({
          x:shape.x+shape.raduis+shape.linewidth/2,
          y:shape.y+shape.raduis+shape.linewidth/2,
        })
        this.selectionsList.push({
          x:shape.x-shape.raduis-shape.linewidth/2,
          y:shape.y+shape.raduis+shape.linewidth/2,
        })
      }
      else if(shape.type=="ellipse"){
        this.ghostCtx.strokeRect(shape.x-shape.linewidth/2-shape.raduisX, shape.y-shape.linewidth/2-shape.raduisY,2*(shape.raduisX)+shape.linewidth,2*(shape.raduisY)+shape.linewidth);
        this.selectionsList.push({
          x:shape.x-shape.raduisX-shape.linewidth/2,
          y:shape.y-shape.raduisY-shape.linewidth/2,
        })
        this.selectionsList.push({
          x:shape.x+shape.raduisX+shape.linewidth/2,
          y:shape.y-shape.raduisY-shape.linewidth/2,
        })
        this.selectionsList.push({
          x:shape.x+shape.raduisX+shape.linewidth/2,
          y:shape.y+shape.raduisY+shape.linewidth/2,
        })
        this.selectionsList.push({
          x:shape.x-shape.raduisX-shape.linewidth/2,
          y:shape.y+shape.raduisY+shape.linewidth/2,
        })
      }
      else if(shape.type=="triangle"){
        this.ghostCtx.strokeRect(shape.x3-shape.linewidth,shape.y1-shape.linewidth,shape.x2-shape.x3+shape.linewidth*2,shape.y3-shape.y1+shape.linewidth*2);
        this.selectionsList.push({
          x:shape.x3-shape.linewidth,
          y:shape.y1-shape.linewidth,
        })
        this.selectionsList.push({
          x:shape.x2+shape.linewidth,
          y:shape.y1-shape.linewidth,
        })
        this.selectionsList.push({
          x:shape.x3-shape.linewidth,
          y:shape.y3+shape.linewidth,
        })
        this.selectionsList.push({
          x:shape.x2+shape.linewidth,
          y:shape.y3+shape.linewidth,
        })

      }
      else if(shape.type=="line"){
        this.ghostCtx.strokeRect(shape.x1,shape.y1,shape.x2-shape.x1,shape.y2-shape.y1);
        this.selectionsList.push({
          x:shape.x1,
          y:shape.y1,
        })
        this.selectionsList.push({
          x:shape.x2,
          y:shape.y2,
        })
      }  
    },
    drawSelectionFrame(shape){
      this.selectionsList=[];
      this.ghostCtx.lineWidth = 2;
          this.ghostCtx.strokeStyle='red';
          this.AddSelectionBoxes(shape);
      for(let i =0 ; i<this.selectionsList.length;i++){
        this.ghostCtx.fillStyle='black';
        this.ghostCtx.fillRect(this.selectionsList[i].x-3,this.selectionsList[i].y-3,6,6);
      }
      this.setProperties(this.linewidth,this.strokeStyle,this.fillStyle);
    },
    deleteShapeFromMain(){
      for(let i=0;i<this.ShapesList.length;i++){
        if(this.ShapesList[i].id == this.selectedShape){
          this.ShapesList.splice(i,1);
          this.clearAll();
          this.repaint();
          break;
        }
      }
    },
    clearAll(){
      this.ctx.clearRect(0,0,this.WIDTH,this.HEIGHT);
    },
    DrawComponent(location , Shape){
      this.setProperties(Shape.linewidth,Shape.strokeStyle,Shape.fillStyle)
      switch(Shape.type){
          case "rectangle":
            this.DrawRect(location,Shape.x , Shape.y , Shape.width , Shape.height);
            break;
          case "square":
            this.DrawSquare(location,Shape.x,Shape.y,Shape.width , Shape.height);
            break;
          case "circle":
            this.DrawCircle(location,Shape.x,Shape.y,Shape.raduis);
            break;
          case "ellipse":
            this.DrawEllipse(location,Shape.x,Shape.y,Shape.raduisX,Shape.raduisY);
            break;
          case "line":
            this.DrawLine(location,Shape.x1,Shape.y1,Shape.x2,Shape.y2);
            break;
          case "triangle":
            this.DrawTriangle(location,Shape.x1,Shape.y1,Shape.x2,Shape.y2,Shape.x3,Shape.y3);
            break;
          default:
            break;
        }
        this.setProperties(this.linewidth,this.strokeStyle,this.fillStyle)
    },
    repaint(){
      for(let i =0 ; i<this.ShapesList.length ; i++){
        this.setProperties(this.ShapesList[i].linewidth,this.ShapesList[i].strokeStyle,this.ShapesList[i].fillStyle);
        this.DrawComponent("ctx",this.ShapesList[i]);
      }
        this.setProperties(this.linewidth,this.strokeStyle,this.fillStyle);
    },
     /* INCOMPLETE METHODES NEEDS TO BE DONE JSUT HERE FOR TESTING */
    setProperties(linewidth,strokeStyle,fillStyle){
      this.ctx.lineWidth=linewidth;
      this.ctx.strokeStyle=strokeStyle;
      this.ctx.fillStyle=fillStyle;
      this.ghostCtx.lineWidth=linewidth;
      this.ghostCtx.strokeStyle=strokeStyle;
      this.ghostCtx.fillStyle=fillStyle;
    },
    DrawRect(location,x,y,width,height){
      if(location =="ctx"){
        this.ctx.fillRect(x,y,width,height);
        this.ctx.strokeRect(x,y,width,height);
      }
      else if(location =="ghostCtx" ){
        this.ghostCtx.fillRect(x,y,width,height);
        this.ghostCtx.strokeRect(x,y,width,height);
      }
    },
    DrawSquare(location,x,y,width,height){
      if(location =="ctx"){
      this.ctx.fillRect(x,y,width,height);
      this.ctx.strokeRect(x,y,width,height);
      }
      else if(location =="ghostCtx" ){
      this.ghostCtx.fillRect(x,y,width,height);
      this.ghostCtx.strokeRect(x,y,width,height);
      }
 
    },
    DrawCircle(location,x,y,raduis){
      if(location =="ctx"){
        this.ctx.beginPath();
        this.ctx.arc(x, y, raduis, 0, 2*Math.PI, true);
        this.ctx.fill();
        this.ctx.stroke();
      }
      else if(location =="ghostCtx" ){
        this.ghostCtx.beginPath();
        this.ghostCtx.arc(x, y, raduis, 0, 2*Math.PI, true);
        this.ghostCtx.fill();
        this.ghostCtx.stroke();
      }
    },
    DrawEllipse(location,x,y,raduisX,raduisY){
      if(location =="ctx"){
        this.ctx.beginPath();
        this.ctx.ellipse(x, y, raduisX , raduisY, 0 , 0, 2*Math.PI, true);
        this.ctx.fill();
        this.ctx.stroke();
      }
      else if(location =="ghostCtx" ){
        this.ghostCtx.beginPath();
        this.ghostCtx.ellipse(x, y, raduisX , raduisY, 0 , 0, 2*Math.PI, true);
        this.ghostCtx.fill();
        this.ghostCtx.stroke();
      }
    },
    DrawLine(location,x1,y1,x2,y2){
      if(location =="ctx"){
        this.ctx.beginPath();
        this.ctx.moveTo(x1,y1);
        this.ctx.lineTo(x2,y2);
        this.ctx.stroke();
        this.ctx.closePath();
      }
      else if(location =="ghostCtx" ){
        this.ghostCtx.beginPath();
        this.ghostCtx.moveTo(x1,y1);
        this.ghostCtx.lineTo(x2,y2);
        this.ghostCtx.stroke();
        this.ghostCtx.closePath();
      }
    },
    DrawTriangle(location,x1,y1,x2,y2,x3,y3){
      if(location =="ctx"){
        this.ctx.beginPath();
        this.ctx.moveTo(x1,y1);
        this.ctx.lineTo(x2,y2);
        this.ctx.lineTo(x3,y3);
        this.ctx.closePath();
        this.ctx.stroke();
        this.ctx.fill();
      }
      else if(location =="ghostCtx" ){
        this.ghostCtx.beginPath();
        this.ghostCtx.moveTo(x1,y1);
        this.ghostCtx.lineTo(x2,y2);
        this.ghostCtx.lineTo(x3,y3);
        this.ghostCtx.closePath();
        this.ghostCtx.stroke();
        this.ghostCtx.fill();
      }
    },
    copyGhostToMain(){
      axios.post('http://localhost:8080/modify',{
        type:this.ShapeInGhost.type,
        linewidth:this.ShapeInGhost.linewidth,
        fillStyle:this.ShapeInGhost.fillStyle,
        strokeStyle:this.ShapeInGhost.strokeStyle,
        id:this.ShapeInGhost.id,
        width:this.ShapeInGhost.width,
        height:this.ShapeInGhost.height,
        raduis:this.ShapeInGhost.raduis,
        raduisX:this.ShapeInGhost.raduisX,
        raduisY:this.ShapeInGhost.raduisY,
        x:this.ShapeInGhost.x,
        y:this.ShapeInGhost.y,
        x1:this.ShapeInGhost.x1,
        x2:this.ShapeInGhost.x2,
        x3:this.ShapeInGhost.x3,
        y1:this.ShapeInGhost.y1,
        y2:this.ShapeInGhost.y2,
        y3:this.ShapeInGhost.y3,
      })
      this.DrawComponent("ctx",this.ShapeInGhost);
      this.ShapesList.push(this.ShapeInGhost);
      this.ShapeInGhost=null;
      this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT);
      this.selectionsList=[];

    },
    MymouseDown(e){
      this.selectedScalingRectangle=this.OnAScalingRectangle;
      this.scaling=false;
      this.moving=false;
      this.creating=false;
      this.myDown=true;
      this.mouseX=e.offsetX;
      this.mouseY=e.offsetY;
      if(this.SelectedForMove){
        this.moving=true;
        if(this.ShapeInGhost.type=="line"){
          this.DiffrenceX=e.offsetX-this.ShapeInGhost.x1;
          this.DiffrenceY=e.offsetY-this.ShapeInGhost.y1;
        }
        else if(this.ShapeInGhost.type=="triangle"){
          this.DiffrenceX=e.offsetX-this.ShapeInGhost.x1;
          this.DiffrenceY=e.offsetY-this.ShapeInGhost.y1;
        }
        else{
          this.DiffrenceX=e.offsetX-this.ShapeInGhost.x;
          this.DiffrenceY=e.offsetY-this.ShapeInGhost.y;
        }
      }
      else if(this.SelectedForResize){
        this.scaling=true;
      }
      else{
        this.creating=true;
      }
    },
    MymouseUp(e){
      if(this.myDown && this.selectedShape !=null && this.mouseX!=e.offsetX && this.mouseY!=e.offsetY){
        if(this.creating){
        this.ctx.strokeStyle=this.strokeStyle;
        this.ctx.fillStyle=this.fillStyle;
        this.ctx.lineWidth = this.linewidth;
        this.ctx.fillStyle=this.fillStyle;
        switch(this.selectedShape){
          case "rectangle":
            this.DrawRect("ctx",this.x,this.y,this.width,this.height);
            this.ShapesList.push({
              type:"rectangle",
              id:this.id,
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              width:this.width,
              height:this.height,
            })
              axios.post('http://localhost:8080/add',{
              type:"rectangle",
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              width:this.width,
              height:this.height,
              }).then(resp => {
                this.ShapesList[this.ShapesList.length-1].id=resp.data
              })
            break;
          case "square":
            this.DrawSquare("ctx",this.x,this.y,this.width,this.height);
            this.ShapesList.push({
              type:"square",
              id:this.id,
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              width:this.width,
              height:this.height,
            });
            axios.post('http://localhost:8080/add',{
              type:"square",
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              width:this.width,
              height:this.height,
              }).then(resp => {
                this.ShapesList[this.ShapesList.length-1].id=resp.data
              })
            break;
          case "line":
            this.DrawLine("ctx",this.x1,this.y1,this.x2,this.y2);
            this.ShapesList.push({
              type:"line",
              id:this.id,
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x1:this.x1,
              y1:this.y1,
              x2:this.x2,
              y2:this.y2,
            });
            axios.post('http://localhost:8080/add',{
              type:"line",
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x1:this.x1,
              y1:this.y1,
              x2:this.x2,
              y2:this.y2,
              }).then(resp => {
                this.ShapesList[this.ShapesList.length-1].id=resp.data
              })
            break;
          case "circle":
            this.DrawCircle("ctx",this.x,this.y,this.raduis);
            this.ShapesList.push({
              type:"circle",
              id:this.id,
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              raduis:this.raduis,
            });
            axios.post('http://localhost:8080/add',{
              type:"circle",
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              raduis:this.raduis,
              }).then(resp => {
                this.ShapesList[this.ShapesList.length-1].id=resp.data
              })
            break;
          case "ellipse":
            this.DrawEllipse("ctx",this.x,this.y,this.raduisX,this.raduisY);
            this.ShapesList.push({
              type:"ellipse",
              id:this.id,
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              raduisX:this.raduisX,
              raduisY:this.raduisY,
            });
            axios.post('http://localhost:8080/add',{
              type:"ellipse",
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x:this.x,
              y:this.y,
              raduisX:this.raduisX,
              raduisY:this.raduisY,
              }).then(resp => {
                this.ShapesList[this.ShapesList.length-1].id=resp.data
              })
            break;
          case "triangle":
            this.DrawTriangle("ctx",this.x1,this.y1,this.x2,this.y2,this.x3,this.y3);
            this.ShapesList.push({
              type:"triangle",
              id:this.id,
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x1:this.x1,
              y1:this.y1,
              x2:this.x2,
              y2:this.y2,
              x3:this.x3,
              y3:this.y3,
            });
            axios.post('http://localhost:8080/add',{
              type:"triangle",
              linewidth:this.linewidth,
              fillStyle:this.fillStyle,
              strokeStyle:this.strokeStyle,
              x1:this.x1,
              y1:this.y1,
              x2:this.x2,
              y2:this.y2,
              x3:this.x3,
              y3:this.y3,
              }).then(resp => {
                this.ShapesList[this.ShapesList.length-1].id=resp.data
              })
            break;
          default:
            break;
        }
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT);
        if(this.ShapeInGhost!=null){
        this.copyGhostToMain();
        }
      }
      }
      this.myDown=false;
      this.moving=false;
      this.scaling=false;
      this.creating=false;
    },
    createMoveModify(e){
      this.selectionHandle(e);
      if(this.myDown==false || this.selectedShape==null)return;
      if(this.scaling){
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT);
        if(this.ShapeInGhost.type=="rectangle"||this.ShapeInGhost.type=="square"){
          if(this.selectedScalingRectangle==0){
            let diffx=e.offsetX-this.ShapeInGhost.x;
            let diffy=e.offsetY-this.ShapeInGhost.y;
            this.ShapeInGhost.x=e.offsetX;
            this.ShapeInGhost.y=e.offsetY;
            this.ShapeInGhost.width-=diffx;
            this.ShapeInGhost.height-=diffy;
          }
          else if(this.selectedScalingRectangle==1){
            let diffwidth=e.offsetX-(this.ShapeInGhost.width+this.ShapeInGhost.x)
            let diffheight=e.offsetY-this.ShapeInGhost.y;
            this.ShapeInGhost.y=e.offsetY;
            this.ShapeInGhost.width+=diffwidth;
            this.ShapeInGhost.height-=diffheight;
          }
          else if(this.selectedScalingRectangle==2){
            let diffwidth=e.offsetX-(this.ShapeInGhost.width+this.ShapeInGhost.x)
            let diffheight=e.offsetY-(this.ShapeInGhost.y+this.ShapeInGhost.height);
            this.ShapeInGhost.width+=diffwidth;
            this.ShapeInGhost.height+=diffheight;
          }
          else if(this.selectedScalingRectangle==3){
            let diffwidth=e.offsetX-(this.ShapeInGhost.x)
            let diffheight=e.offsetY-(this.ShapeInGhost.y+this.ShapeInGhost.height);
            this.ShapeInGhost.x=e.offsetX;
            this.ShapeInGhost.width-=diffwidth;
            this.ShapeInGhost.height+=diffheight;
          }
 
        }
        else if(this.ShapeInGhost.type=="circle"){
          if(e.offsetX-this.ShapeInGhost.x-this.ShapeInGhost.raduis>e.offsetY-this.ShapeInGhost.y-this.ShapeInGhost.raduis){
            this.ShapeInGhost.raduis+=e.offsetX-this.ShapeInGhost.x-this.ShapeInGhost.raduis;
            this.ShapeInGhost.raduis=Math.abs(this.ShapeInGhost.raduis)
          }
          else{
            this.ShapeInGhost.raduis+=e.offsetY-this.ShapeInGhost.y-this.ShapeInGhost.raduis
            this.ShapeInGhost.raduis=Math.abs(this.ShapeInGhost.raduis)
          }
        }
        else if(this.ShapeInGhost.type=="ellipse"){
            this.ShapeInGhost.raduisY+=e.offsetY-this.ShapeInGhost.y-this.ShapeInGhost.raduisY
            this.ShapeInGhost.raduisY=Math.abs(this.ShapeInGhost.raduisY)
            this.ShapeInGhost.raduisX+=e.offsetX-this.ShapeInGhost.x-this.ShapeInGhost.raduisX;
            this.ShapeInGhost.raduisX=Math.abs(this.ShapeInGhost.raduisX)
        }
        else if(this.ShapeInGhost.type=="line"){
          if(this.selectedScalingRectangle==0){
            this.ShapeInGhost.x1=e.offsetX;
            this.ShapeInGhost.y1=e.offsetY;
          }
          else if(this.selectedScalingRectangle==1){
            this.ShapeInGhost.x2=e.offsetX;
            this.ShapeInGhost.y2=e.offsetY;
          }
        }
        else if(this.ShapeInGhost.type=="triangle"){
          if(this.selectedScalingRectangle==0){
            let diffx=e.offsetX-this.selectionsList[0].x;
            this.ShapeInGhost.y1=e.offsetY;
            this.ShapeInGhost.x3+=diffx;
            this.ShapeInGhost.x2-=diffx
          }
          else if(this.selectedScalingRectangle==1){
            let diffx=e.offsetX-this.selectionsList[1].x;
            this.ShapeInGhost.y1=e.offsetY;
            this.ShapeInGhost.x3-=diffx;
            this.ShapeInGhost.x2+=diffx
          }
          else if(this.selectedScalingRectangle==2){
            this.ShapeInGhost.x3=e.offsetX;
            this.ShapeInGhost.y3=e.offsetY;
            this.ShapeInGhost.y2=e.offsetY;
            this.ShapeInGhost.x1=this.ShapeInGhost.x2 +(this.ShapeInGhost.x3-this.ShapeInGhost.x2)/2;
          }
          else if(this.selectedScalingRectangle==3){
            this.ShapeInGhost.x2=e.offsetX;
            this.ShapeInGhost.y2=e.offsetY;
            this.ShapeInGhost.y3=e.offsetY;
            this.ShapeInGhost.x1=this.ShapeInGhost.x2 +(this.ShapeInGhost.x3-this.ShapeInGhost.x2)/2;
          }
        }
          this.DrawComponent("ghostCtx",this.ShapeInGhost);
          this.drawSelectionFrame(this.ShapeInGhost);  
      }
      else if(this.moving){
        this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT);
        if(this.ShapeInGhost.type=="line"){
        let lenx=this.ShapeInGhost.x2-this.ShapeInGhost.x1;
        let leny=this.ShapeInGhost.y2-this.ShapeInGhost.y1;
        this.ShapeInGhost.x1 = e.offsetX - this.DiffrenceX;
        this.ShapeInGhost.y1 = e.offsetY - this.DiffrenceY;
        this.ShapeInGhost.x2= e.offsetX - this.DiffrenceX+lenx;
        this.ShapeInGhost.y2 = e.offsetY - this.DiffrenceY+leny;

        }
        else if(this.ShapeInGhost.type=="triangle"){
        let lenx1x2 = this.ShapeInGhost.x2-this.ShapeInGhost.x1;
        let lenx1x3 = this.ShapeInGhost.x3-this.ShapeInGhost.x1;
        let leny1y2 = this.ShapeInGhost.y2-this.ShapeInGhost.y1;
        let leny1y3 = this.ShapeInGhost.y3-this.ShapeInGhost.y1;
        this.ShapeInGhost.x1 = e.offsetX-this.DiffrenceX;
        this.ShapeInGhost.y1 = e.offsetY-this.DiffrenceY;
        this.ShapeInGhost.x2 = e.offsetX-this.DiffrenceX + lenx1x2;
        this.ShapeInGhost.y2 = e.offsetY-this.DiffrenceY + leny1y2;
        this.ShapeInGhost.x3 = e.offsetX-this.DiffrenceX + lenx1x3;
        this.ShapeInGhost.y3 = e.offsetY-this.DiffrenceY + leny1y3;
        }
        else{
        this.ShapeInGhost.x = e.offsetX-this.DiffrenceX;
        this.ShapeInGhost.y = e.offsetY-this.DiffrenceY;
        }
        this.DrawComponent("ghostCtx",this.ShapeInGhost);
        this.drawSelectionFrame(this.ShapeInGhost);
      }
      else if(this.creating){
      if(this.selectedShape==null || !isNaN(this.selectedShape))return;
      this.ghostCtx.clearRect(0,0,this.WIDTH,this.HEIGHT);
      this.ghostCtx.lineWidth = this.linewidth;
      this.ghostCtx.fillStyle=this.fillStyle;
      this.ghostCtx.strokeStyle=this.strokeStyle;
      let signX , signY;
      switch(this.selectedShape){
        case "rectangle":
          this.x=this.mouseX;
          this.y=this.mouseY;
          this.width = e.offsetX - this.x;
          this.height = e.offsetY - this.y;
          this.DrawRect("ghostCtx",this.x,this.y,this.width,this.height);
          break;
        case "square":
          signX=(e.offsetX - this.x)/Math.abs(e.offsetX - this.x);
          signY=(e.offsetY - this.y)/Math.abs(e.offsetY - this.y);
          this.x=this.mouseX;
          this.y=this.mouseY;          
          if(e.offsetX - this.x > e.offsetY - this.y){
            this.width = Math.abs(e.offsetY - this.y)
            this.height = Math.abs(e.offsetY - this.y)
          }
          else{
            this.width = Math.abs(e.offsetX - this.x)
            this.height = Math.abs(e.offsetX - this.x)
          }
          this.width=this.width*signX;
          this.height=this.height*signY;
          this.DrawSquare("ghostCtx",this.x,this.y,this.width,this.height);
          break;
        case "line":
          this.x1=this.mouseX;
          this.y1=this.mouseY;
          this.x2=e.offsetX;
          this.y2=e.offsetY;
          this.DrawLine("ghostCtx",this.x1,this.y1,this.x2,this.y2);
          break;
        case "circle":
          this.x=this.mouseX;
          this.y=this.mouseY;
           if(e.offsetX - this.x < e.offsetY - this.y){
            this.raduis = e.offsetY - this.y;
          }
          else{
            this.raduis = e.offsetX - this.x;
          }
          this.raduis = Math.abs(this.raduis);
          this.DrawCircle("ghostCtx",this.x,this.y,this.raduis);
          break;
        case "ellipse":
          this.x=this.mouseX;
          this.y=this.mouseY;
          this.raduisX=Math.abs(e.offsetX - this.x);
          this.raduisY=Math.abs(e.offsetY - this.y);
          this.DrawEllipse("ghostCtx",this.x,this.y,this.raduisX,this.raduisY);
          break;
        case "triangle":
          this.x1=(e.offsetX+this.mouseX)/2;
          this.y1=this.mouseY;
          this.x2=e.offsetX;
          this.y2=e.offsetY;
          this.x3=this.mouseX;
          this.y3=e.offsetY;
          this.DrawTriangle("ghostCtx",this.x1,this.y1,this.x2,this.y2,this.x3,this.y3);
          break;
        default:
          return;
      }
      }
    }
  },
}
</script>

<style>
#app{
  display:flex;
  justify-content: left;
}
button{
  font-size:12px;
  border: 1px solid black;
	outline: none;
}
#save-load-new{
  display: grid;
  height: auto;
	grid-template-columns: repeat(2, 50%);
	grid-template-rows:repeat(2, 30px);
}
#selectShapePanel{
  display: grid;
  height:auto;
	grid-template-columns: repeat(2, 50%);
	grid-template-rows: repeat(4, 30px);
}
#load{
  display:grid;
  grid-template-columns: repeat(1, 100%);
  align-content: flex-start;
  overflow: scroll;
  height:175px;
  width:115px;
  background-color: gainsboro;
}
#ShapesHistory{
  overflow: scroll;
  height: 345px;
  width:115px;
  background-color: gainsboro;
}
#ShapesHistory >button{
  width:100px;
  height: 40px;
}
button:hover{
  font-size:13px;
  cursor:pointer;
}
.span-two{
  grid-column: span 2;
}

#PaintPanel{
  display:inline-block;
  width:800px; 
  height:500px;
  position: relative ;
  border : 2px solid black;
}
#color-Pickers{
  display:flex;
  justify-content: center;
}
#lineWidthResize{
  display:grid;
  grid-template-columns: repeat(2, 50%);
	grid-template-rows: repeat(1, 100%);
}
#MainCanvas{
  position: absolute;
  z-index: 1;
}
#GhostCanvas{
  position: relative;
  z-index: 20;
}
#modifyShapes{
  width:115px;
  height:500px;
  border:2px solid black;
}
#toolsPanel{
  display:inline-block;
  border:2px solid black;
  border-right-style: 1px solid black;
  height:500px;
  width:115px;
}
p{
  font-size: 15px;
  text-align: center;
  width:auto;
  height:auto;
  margin:0;

}

</style>
