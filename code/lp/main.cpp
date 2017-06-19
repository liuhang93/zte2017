#include <iostream>
#include <queue>
#include <cstring>
#include <algorithm>
#include <vector>
#include <fstream>
#include <sstream>
#include <stdlib.h>
#include <time.h>
#include "lp_lib.h"


using namespace std;
#define maxn 500000
#define INF 1000007
#define NODE_UPPER_LIMIT 100000


int sideNum;
int nodeNum;
int startNode;
int desNode;
int newStartNode;
int newDesNode;
int maxVisitNodeLimit;
int nodeNumList[maxn];
string myout;
vector<string> outCsv;
string fuckMinVisitNode;

struct node {
	int hao, w;
};
bool operator <(node a, node b) {
	return (a.w > b.w);
}
//dj类用于储存图，以及计算最短路径
struct dj {
    int e,                //记录点的个数
    to[maxn],           //链式前向星存图
    bi[maxn],
    ne[maxn],
    ww[maxn],           //边的距离
    p[maxn],            //记录顶点在求最短路径时是否被访问过
    dis[maxn],          //记录点之间的距离
    a[maxn];            //记录路径
	void init() {
	    e = 0;
		memset(p, 0, sizeof(p));
		memset(dis, 0, sizeof(dis));
		memset(to, 0, sizeof(to));
		memset(ne, 0, sizeof(ne));
		memset(bi, 0, sizeof(bi));
		memset(ww, 0, sizeof(ww));
		memset(a, 0, sizeof(a));
	}
	void add(int x, int y, int z) {
		to[++e] = y;
		ne[e] = bi[x];
		bi[x] = e;
		ww[e] = z;
	}
	void delSide(int x, int y) {
		for (int i = bi[x];i;i = ne[i]) {
			if (to[i] == y) {
				ww[i] = INF;
			}
		}
	}
    int sideValue(int x , int y){
        for (int i = bi[x];i;i = ne[i]) {
			if (to[i] == y) {
				return ww[i];
			}
		}
        return INF;
    }
	void dijie(int h, int g , int m) {
		for (int i = 1;i <= m;i++) {
			dis[i] = INF;
			a[i] = 0;
			p[i] = 0;
		}
		priority_queue<node> o;
		dis[h] = 0;
		o.push({ h, 0 });
		while (!o.empty()) {
			node flag = o.top();
			o.pop();
			int u = flag.hao;
			if (p[u])continue;
			int v = flag.w;
			p[u] = 1;
			dis[u] = v;
            if(u==g) return;
			for (int i = bi[u];i;i = ne[i]) {
				int t = to[i];
				if (dis[t] > dis[u] + ww[i])
				{
					dis[t] = dis[u] + ww[i];
					a[t] = u;
				}
				o.push({ t, dis[t] });
			}
		}
	}
	void findPath(int startPoint , int  terminalPoint , vector<int> &path){
        path.push_back(terminalPoint);
        while (path.back() != 0) path.push_back(a[path.back()]);
        path.pop_back();
        if (path.back() != startPoint) path.push_back(startPoint);
        reverse(path.begin(),path.end());
	}
}s;

vector<string> split(const string& src, string separate_character)  
{  
    vector<string> strs;  
    int separate_characterLen = separate_character.size();//分割字符串的长度,这样就可以支持如“,,”多字符串的分隔符  
    int lastPosition = 0, index = -1;  
    while (-1 != (index = src.find(separate_character, lastPosition)))  
    {  
        strs.push_back(src.substr(lastPosition, index - lastPosition));  
        lastPosition = index + separate_characterLen;  
    }  
    string lastString = src.substr(lastPosition);//截取最后一个分隔符后的内容  
    if (!lastString.empty())  
        strs.push_back(lastString);//如果最后一个分隔符后还有内容就入队  
    return strs;  
}  

void readTopo(char* filename,vector<vector<int> > &img){
    string value;
    ifstream infile(filename);
    while(infile.good()){  
        getline(infile,value); 
        vector<string> strs = split(value,",");  
        if(strs.size() >= 4){
            //s.add(atoi(strs[1].c_str())+1,atoi(strs[2].c_str())+1,atoi(strs[3].c_str()));
            //s.add(atoi(strs[2].c_str())+1,atoi(strs[1].c_str())+1,atoi(strs[3].c_str()));
            
            img[0].push_back(atoi(strs[1].c_str())+1);
            img[1].push_back(atoi(strs[2].c_str())+1);
            img[2].push_back(atoi(strs[3].c_str()));
            img[0].push_back(atoi(strs[2].c_str())+1);
            img[1].push_back(atoi(strs[1].c_str())+1);
            img[2].push_back(atoi(strs[3].c_str()));
            //cout<<atoi(strs[2].c_str())<<"  "<<atoi(strs[1].c_str())<<"  "<<atoi(strs[3].c_str())<<endl;
            sideNum += 2;
        }
    }
    //cout<<sideNum<<endl;
    return;
}

void readDemand(char* filename,vector<int> &mustNode,vector<int> &mustSide,vector<int> &importNode,vector<int> &avoidSide){
    string value;
    ifstream infile(filename);
    //read first line.
    getline(infile,value);
    vector<string> strs0 = split(value,",");
    startNode =  atoi(strs0[0].c_str())+1;
    desNode = atoi(strs0[1].c_str())+1;
    importNode.push_back(atoi(strs0[0].c_str())+1);
    importNode.push_back(atoi(strs0[1].c_str())+1);
    //read second line.
    getline(infile,value);
    vector<string> strs1 = split(value,",");
    for(int i = 0 ; i < int(strs1.size()) ; i++){
        mustNode.push_back(atoi(strs1[i].c_str())+1);
        importNode.push_back(atoi(strs1[i].c_str())+1);
    }
    //read third line.
    getline(infile,value);
    vector<string> strs2 = split(value,"|");
    for(int i = 0 ; i < int(strs2.size()) ; i++){
        if(strs2[0][0]=='N') break;
        vector<string> eachNode = split(strs2[i],",");
        importNode.push_back(atoi(eachNode[0].c_str())+1);
        importNode.push_back(atoi(eachNode[1].c_str())+1);
        mustSide.push_back(atoi(eachNode[0].c_str())+1);
        mustSide.push_back(atoi(eachNode[1].c_str())+1);
    }
    for(int i = 0 ; i < importNode.size() ; i++){
        for(int j=i+1 ; j < importNode.size() ; j++){
            if(importNode[i]==importNode[j]){
                importNode.erase(importNode.begin()+j);
                j--;
            }
        }
    }
    //mustSide.push_back(startNode);
    //mustSide.push_back(desNode);
    //read fourth line.
    getline(infile,value);
    vector<string> strs3 = split(value,"|");
    for(int i = 0 ; i < int(strs3.size()) ; i++){
        if(strs3[0][0]=='N') break;
        vector<string> eachNode = split(strs3[i],",");
        //s.delSide(atoi(eachNode[0].c_str())+1,atoi(eachNode[1].c_str())+1);
        //s.delSide(atoi(eachNode[1].c_str())+1,atoi(eachNode[0].c_str())+1);
        avoidSide.push_back(atoi(eachNode[0].c_str())+1);
        avoidSide.push_back(atoi(eachNode[1].c_str())+1);
    }
    //read fifth line.
    getline(infile,value);
    maxVisitNodeLimit = atoi(value.c_str());
    return;
}

void creatNewImg(vector<vector<int> > &newImg,vector<vector<int> > &newEachPath,vector<vector<int> > &img,vector<int> &importNode,vector<int> &mustSide,vector<int> &avoidSide){
    s.init();
    for(int i = 0 ; i < int(img[0].size()) ; i++){
        s.add(img[0][i],img[1][i],img[2][i]);
    }
    for(int i =0 ; i < int(avoidSide.size()) ; i+=2){
        s.delSide(avoidSide[i],avoidSide[i+1]);
        s.delSide(avoidSide[i+1],avoidSide[i]);
    }
    // cout<<img[0].size()<<endl; 
    // for(int i = 0 ; i < img[0].size() ; i++){
    //     cout<<img[0][i]<<"\t"<<img[1][i]<<"\t"<<img[2][i]<<"\n";
    // }
    for(int i = 0 ; i < int(importNode.size()) ; i++){
        for(int j = 0 ; j < int(importNode.size()) ; j++){
            if(i != j){
                s.dijie(importNode[i],importNode[j],sideNum*2);
                vector<int> eachSide(3);
                eachSide[0] = importNode[i];
                eachSide[1] = importNode[j];
                eachSide[2] = s.dis[importNode[j]];
                newImg.push_back(eachSide);

                vector<int> path;
                s.findPath(importNode[i],importNode[j],path);
                path.erase(path.begin());
                newEachPath.push_back(path);
            }
        }
    }
    int midNodeNum = NODE_UPPER_LIMIT;
    for(int i = 0 ; i < mustSide.size() ; i+=2){
        vector<int> midSide1(3);
        midSide1[0] = mustSide[i];
        midSide1[1] = ++midNodeNum;
        midSide1[2] = s.sideValue(mustSide[i],mustSide[i+1]);
        newImg.push_back(midSide1);
        vector<int> path1(1,midNodeNum);
        newEachPath.push_back(path1);

        vector<int> midSide2(3);
        midSide2[0] = midNodeNum;
        midSide2[1] = mustSide[i+1];
        midSide2[2] = 0;
        newImg.push_back(midSide2);
        vector<int> path2(1,mustSide[i+1]);
        newEachPath.push_back(path2);

        vector<int> midSide3(3);
        midSide3[0] = midNodeNum;
        midSide3[1] = mustSide[i];
        midSide3[2] = s.sideValue(mustSide[i],mustSide[i+1]);
        newImg.push_back(midSide3);
        vector<int> path3(1,mustSide[i]);
        newEachPath.push_back(path3);

        vector<int> midSide4(3);
        midSide4[0] = mustSide[i+1];
        midSide4[1] = midNodeNum;
        midSide4[2] = 0;
        newImg.push_back(midSide4);
        vector<int> path4(1,midNodeNum);
        newEachPath.push_back(path4);
    }
}

int tight(vector<vector<int> > &newImg){
    int NewNodeNum = 0;
    for(int i = 0 ; i < int(newImg.size()) ;  i++){
        newImg[i].push_back(-1);
        newImg[i].push_back(-1);
        for(int j = 0 ; j < i ; j++){
            if(newImg[i][0]==newImg[j][0]) newImg[i][3]=newImg[j][3];
            if(newImg[i][1]==newImg[j][0]) newImg[i][4]=newImg[j][3];
            if(newImg[i][0]==newImg[j][1]) newImg[i][3]=newImg[j][4];
            if(newImg[i][1]==newImg[j][1]) newImg[i][4]=newImg[j][4];
        }
        if(newImg[i][3]==-1) newImg[i][3]=NewNodeNum++;
        if(newImg[i][4]==-1) newImg[i][4]=NewNodeNum++;
        if(newImg[i][0]==startNode) newStartNode=newImg[i][3];
        if(newImg[i][1]==desNode) newDesNode=newImg[i][4];
    }
    for(int i = 0 ; i < int(newImg.size()) ;  i++){
        nodeNumList[newImg[i][4]] = newImg[i][1];
        nodeNumList[newImg[i][3]] = newImg[i][0];
    }
    return NewNodeNum;
}


int TSP(vector<vector<int> > &newImg , vector<int> &visitOrder){
    // for(int i = 0 ; i < newImg.size() ; i++){
    //     cout<<newImg[i][0]-1<<"\t"<<newImg[i][1]-1<<"\t"<<endl;
    // }
    // cout<<endl;
    int NewNodeNum = tight(newImg);
    // for(int i = 0 ; i < newImg.size() ; i++){
    //     cout<<newImg[i][0]-1<<"\t"<<newImg[i][1]-1<<"\t"<<newImg[i][2]<<"\t"<<newImg[i][3]<<"\t"<<newImg[i][4]<<"\t"<<endl;
    // }
    // cout<<NewNodeNum<<endl;
    
    vector<vector<int> > newImgMatrix(NewNodeNum,vector<int>(NewNodeNum,INF));
    for(int i = 0 ; i < newImg.size() ; i++){
        newImgMatrix[newImg[i][3]][newImg[i][4]]=newImg[i][2];
    }
    //accelerate(newImgMatrix);
    // for(int i = 0 ; i < newImgMatrix.size() ; i++){
    //     for(int j = 0 ; j <newImgMatrix[0].size() ; j++){
    //         cout<<newImgMatrix[i][j]<<"\t";
    //     }
    //     cout<<endl;
    // }
    // cout<<endl;
    // cout<<"newStartNode:"<<newStartNode<<"   "<<"newDesNode:"<<newDesNode<<endl;
    // cout<<"startNode:"<<startNode<<"   "<<"desNode:"<<desNode<<endl<<endl;

    //Linear programming
    int xNum = NewNodeNum*NewNodeNum+NewNodeNum;
    lprec *lp;
    REAL row[xNum+1];
    lp = make_lp(0,xNum);
    set_add_rowmode(lp, TRUE);
    set_verbose(lp, 3);
    set_minim(lp);

    int temp = 0 ; 
    for(int z = 1 ; z < xNum+1 ;z++) row[z] = 0;
    for(int i = 0 ; i < newImgMatrix.size() ; i++){
        for(int j = 0 ; j < newImgMatrix[0].size() ; j++){
            row[++temp] = newImgMatrix[i][j];
        }
    } 
    set_obj_fn(lp, row);
    
    for(int i = 0 ; i < NewNodeNum ; i++){
        for(int z = 1 ; z < xNum+1 ;z++) row[z] = 0;
        for(int j = i*NewNodeNum+1 ; j < (i+1)*NewNodeNum+1 ; j++){
            row[j] = 1;
        }
        if(i != newDesNode){
            add_constraint(lp, row, EQ, 1);
        }else{
            add_constraint(lp, row, EQ, 0);
        }
    }

    for(int i = 0 ; i < NewNodeNum ; i++){
        for(int z = 1 ; z < xNum+1 ;z++) row[z] = 0;
        for(int j = i+1 ; j < NewNodeNum*NewNodeNum+1 ; j+=NewNodeNum){
            row[j] = 1;
        }
        if(i != newStartNode){
            add_constraint(lp, row, EQ, 1);
        }else{
            add_constraint(lp, row, EQ, 0);
        }
    }

    for(int i = 0 ; i < NewNodeNum ; i++){
        for(int j = 0 ; j < NewNodeNum ; j++){
            if(i != j){
                for(int z = 1 ; z < xNum+1 ;z++) row[z] = 0;
                row[NewNodeNum*NewNodeNum+i+1]=1;
                row[NewNodeNum*NewNodeNum+j+1]=-1;
                row[i*NewNodeNum+j+1]=NewNodeNum;
                add_constraint(lp, row, LE, NewNodeNum-1);
            }
        }
    }

    for(int i = 1 ; i < xNum+1 ; i++){
        set_int(lp,i,true);
    }

    for(int i = 1 ; i < NewNodeNum*NewNodeNum+1 ; i++){
        set_bounds(lp, i, 0, 1);
        if(newImgMatrix[(i-1)/NewNodeNum][(i-1)%NewNodeNum] == INF){
            set_bounds(lp, i, 0, 0);
        }
    }

    for(int i = NewNodeNum*NewNodeNum+1 ; i < xNum+1 ; i++){
        set_bounds(lp, i, 1, NewNodeNum);
    }

    set_bounds(lp, NewNodeNum*NewNodeNum+1+newStartNode, 1, 1);
    set_bounds(lp, NewNodeNum*NewNodeNum+1+newDesNode, NewNodeNum, NewNodeNum);

    set_add_rowmode(lp, FALSE);

    solve(lp);
    // print_solution(lp, 1);
    get_variables(lp, row);
    for(int i = 0 ; i < NewNodeNum ; i++){
        visitOrder.push_back(row[NewNodeNum*NewNodeNum+i]);
    }
    int objective = get_objective(lp);
    
    if(row[NewNodeNum*NewNodeNum+1] == 0){
        cout<<"无法寻找路径,可能的原因:\n1,起点与终点不连通\n2,输入错误\n";
        exit(1);
    }
    
    delete_lp(lp);
    return objective;
}

vector<int> getResult(vector<int> &visitOrder, vector<vector<int> > &newImg, vector<vector<int> > &newEachPath, int OptimalSolution ){
    vector<int> newResult;
    vector<int> result;
    result.push_back(startNode);
    for(int i = 1 ; i < visitOrder.size()+1 ; i++){
        for(int j = 0 ; j < visitOrder.size() ; j++){
            if(visitOrder[j] == i && nodeNumList[j] < NODE_UPPER_LIMIT) newResult.push_back(nodeNumList[j]);
        }
    }
    // for(int i=0 ; i <newResult.size() ; i++){
    //     cout<<newResult[i]<<"  ";
    // }
    for(int i = 0 ; i < newResult.size()-1 ; i++){
        for(int j = 0 ; j < newImg.size() ; j++){
            if(newImg[j][0] == newResult[i] && newImg[j][1] == newResult[i+1]){
                result.insert(result.end(),newEachPath[j].begin(),newEachPath[j].end());
            }
        }
    }
    return result;
}


void writeResult(vector<int> result , char *csvResult , vector<vector<int> > &imgCopy){
    string value;
    string fuckCsv = "最短路径:";
    for(int i = 0 ; i < result.size() ; i++){
        cout<<result[i]-1;
        ostringstream oss;
        oss<<result[i]-1;
        value+=oss.str();
        if(i != result.size()-1){
            value+="|";
            cout<<"|";
        }
    }
    myout+=value;
    myout+='\n';
    cout<<endl;
    fuckCsv+=value;
    fuckCsv+="; 费用和: ";
    int distance = 0;
    for(int i = 0 ; i <result.size()-1 ; i++){
        for(int j = 0 ; j < imgCopy[0].size() ; j++){
            if(result[i] == imgCopy[0][j] && result[i+1] == imgCopy[1][j]){
                distance+=imgCopy[2][j];
            }
        }
    }
    cout<<"distance:"<<distance<<endl;
    myout+="distance:";
    ostringstream oss1;
    oss1<<distance;
    myout+=oss1.str();
    myout+='\n';
    fuckCsv+=oss1.str();
    fuckCsv+="; 节点数: ";

    cout<<"visit node number:"<<result.size()<<endl;
    myout+="visit node number:";
    ostringstream oss2;
    oss2<<result.size();
    myout+=oss2.str();
    myout+="\n";
    fuckCsv+=oss2.str();
    fuckMinVisitNode=oss2.str();

    outCsv.push_back(fuckCsv);

    
    return;
}


int round(vector<vector<int> > &img, vector<int> &importNode, vector<int> &mustSide, vector<int> &avoidSide,vector<int> &result){
    vector<vector<int> > newImg;
    vector<vector<int> > newEachPath;
    vector<int> visitOrder;
    creatNewImg(newImg,newEachPath,img,importNode,mustSide,avoidSide);
    int OptimalSolution = TSP(newImg,visitOrder);
    result = getResult(visitOrder,newImg,newEachPath,OptimalSolution);
    return OptimalSolution;
}

vector<vector<int> > getAvaImg(vector<vector<int> > img){
    for(int i = 0 ; i < img[2].size() ; i++){
        img[2][i]=1;
    }
    return img;
}

void setImgValue(vector<vector<int> > &img , int addNum){
    for(int i = 0 ; i < img[2].size() ; i++){
        img[2][i]+=addNum;
        //cout<<img[2][i]<<"  ";
    }
    //cout<<endl;
}

vector<vector<int> > copyTopo(vector<vector<int> > &img){
    vector<vector<int> > imgCopy(3,vector<int>(img[0].size())); 
    for(int i = 0 ; i < img[2].size() ; i++){
        imgCopy[0][i]=img[0][i];
        imgCopy[1][i]=img[1][i];
        imgCopy[2][i]=img[2][i];
    }
    return imgCopy;
}

void out2csv(char *csvResult ,int flag){
    ofstream outfile;
    outfile.open(csvResult,ios::out);
    //outfile<<value; 
    string fuckout;
    if(flag==0){
        fuckout+="不存在满足所有限制条件的解:节点数据限制不满足,最少节点数为";
        fuckout+=fuckMinVisitNode;
        fuckout+="\n";
        fuckout+="节点树最少的路径:";
        fuckout+=outCsv[1];
        fuckout+="\n";
        fuckout+="费用和最少的路径:";
        fuckout+=outCsv[0];
        fuckout+="\n";
        cout<<fuckout;
        outfile<<fuckout; 
    }else{
        fuckout+="存在满足所有限制条件的解\n";
        fuckout+="最优路径:";
        fuckout+=outCsv[outCsv.size()-1];
        fuckout+="\n";
        cout<<fuckout;
        outfile<<fuckout; 
    }
    outfile.close(); 
}

int main(int argc,char *argv[]){ 
    clock_t start,end; 
    start = clock();
    int superFlag = 0;
    if(argc != 4){
        cout<<"argument err!\nplease input:topoPath demandPath resultPath\n";
        myout+="argument err!\nplease input:topoPath demandPath resultPath\n";
        exit(1);
    }
    vector<vector<int> > img(3); 
    readTopo(argv[1],img);
    vector<vector<int> > imgCopy = copyTopo(img);
    vector<int> mustNode;
    vector<int> mustSide;
    vector<int> importNode;         
    vector<int> avoidSide;
    vector<int> result;
    readDemand(argv[2],mustNode,mustSide,importNode,avoidSide);
    
    int OptimalSolution = round(img,importNode,mustSide,avoidSide,result);

    if(result.size() > maxVisitNodeLimit){
        cout<<"\n暂时未找到最优解（可能无解），以下为不限制访问次数时的近似解："<<endl;
        myout+="\n暂时未找到最优解（可能无解），以下为不限制访问次数时的近似解：\n";
        writeResult(result, argv[3],imgCopy);
        vector<vector<int> > avaImg = getAvaImg(img);
        OptimalSolution = round(avaImg,importNode,mustSide,avoidSide,result);
        if(result.size() > maxVisitNodeLimit){
            cout<<"\n该题被证明无解，以下为访问次数最少情况下的近似解："<<endl;
            myout+="\n该题被证明无解，以下为访问次数最少情况下的近似解：\n";
            writeResult(result, argv[3],imgCopy);
        }else{
            int maxCount = 0;
            cout<<endl;
            do{
                maxCount++;
                setImgValue(img,1);
                OptimalSolution = round(img,importNode,mustSide,avoidSide,result);
                cout<<"迭代寻找中...           迭代次数:"<<maxCount<<endl;
            }while(result.size() > maxVisitNodeLimit && maxCount !=2);
			cout<<"\n找到最优解："<<endl;
            myout+="\n找到最优解：\n";
            superFlag=1;
            writeResult(result, argv[3],imgCopy);
        }
    }else{
		cout<<"\n找到最优解："<<endl;
        myout+="\n找到最优解：\n";
        superFlag=1;
        writeResult(result, argv[3],imgCopy);
    }
    end = clock();
    cout<<endl<<"useing time:"<<((double)end-start)/CLOCKS_PER_SEC<<"s"<<endl; 
    out2csv(argv[3],superFlag);
    // std::cout<<"\33[2J"<<std::endl;
    // cout<<"搜索结果如下：\n";
    // cout<<myout;
    return 0;  
}  