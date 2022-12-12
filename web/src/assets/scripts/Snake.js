import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject{
    constructor(info, gamemap){
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)]; //存放蛇的身体
        this.next_cell = null; //下一步的目的位置

        this.speed = 5;
        this.direction = -1; //-1表示没有指令，0，1，2,3表示上右下左
        this.status = "idle"; //idle表示进制， move表示正在移动，die表示死亡

        this.dr = [-1, 0 ,1 , 0];
        this.dc = [0, 1, 0 ,-1];

        this.step = 0;
    }

    start(){

    }

    next_step(){
        // 将蛇的状态变为下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.direction = -1;
        this.status = "move";
        this.step++;
    }

    update_move(){ 

        // this.cells[0].x += this.speed * this.timedelta / 1000;
        // this.cells[0].y -= this.speed * this.timedelta / 1000;

        
    }

    update(){
        
        this.render();
        this.update_move();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        for(const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2,0, Math.PI * 2);
            ctx.fill();
        }
    }
}