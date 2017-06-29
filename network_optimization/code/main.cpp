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
#include <cmath>

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

void readTopo(char* filename,vector<vector<int> > &img,vector<int> &firstLine){
    string value;
    ifstream infile(filename);
    getline(infile,value); 
    vector<string> strs = split(value,",");  
    for(int i = 0 ; i <strs.size() ; i++){
        firstLine.push_back(atoi(strs[i].c_str()));
    }
    
    while(infile.good()){  
        getline(infile,value); 
        strs = split(value,",");  
        vector<int> eachSide;
        eachSide.push_back(atoi(strs[0].c_str())-atoi(strs[2].c_str()));
        eachSide.push_back(atoi(strs[1].c_str())-atoi(strs[2].c_str()));
        for(int j = 3; j < strs.size() ; j++){
            // eachSide.push_back(atoi(strs[j].c_str()));
            eachSide.push_back(atoi(strs[j].c_str())==0 ? 0:1);
        }

        // for(int i = 0 ;i < strs.size() ; i++){
        //     cout<<strs[i]<<'\t';
        // }
        // cout<<endl;

        img.push_back(eachSide);
    }
    img.pop_back();
    return;
}

void XXGH(vector<vector<int> > &img){
    //Linear programming
    int xNum = img[0].size()-2;
    lprec *lp;
    REAL row[xNum+1];
    lp = make_lp(0,xNum);

    set_add_rowmode(lp, TRUE);
    //set_verbose(lp, 3);
    set_minim(lp);

    for(int z = 1 ; z < xNum+1 ;z++) row[z] = 1;
    set_obj_fn(lp, row);

    for(int i = 0 ; i < img.size() ; i++){
        for(int z = 1 ; z < xNum+1 ;z++) row[z] = 0;
        for(int j = 1 ; j < xNum+1 ;j++){
            row[j] = img[i][j+1];
        }
        for(int ii = 1 ; ii < xNum+1 ; ii++){
                cout<<row[ii];
        }
        cout<<endl;
        add_constraint(lp, row, GE, img[i][0]);
        cout<<img[i][0]<<endl;
        add_constraint(lp, row, LE, img[i][1]);
        cout<<img[i][1]<<endl;
        set_bounds(lp, i+1, -20, 100);
    }
    vector<int> fuck;

    //fuck.push_back(0);
    // fuck.push_back(3);
    // fuck.push_back(7);
    // fuck.push_back(6);
    //fuck.push_back(1);


    for(int i = 0 ; i < fuck.size() ; i++){
        set_bounds(lp, fuck[i]+1, 0, 0);
    }


    set_add_rowmode(lp, FALSE);


    // solve(lp);
    // print_solution(lp, 1);
    // set_bounds(lp, 7, 0, 0);
    int flag;
    for(int kk=0 ; kk < 5 ;kk++){
        flag = 0 ;
        solve(lp);
        print_solution(lp, 1);
        get_variables(lp, row);
        int min = INF;
        int minNum = INF;
        int sum=0;
        for(int i= 0 ; i < xNum ; i++){
            cout<<row[i]<<endl; 
            sum+=row[i];
            if(min>row[i]){
                min=row[i];
                minNum=i;
            }
        }
        set_bounds(lp, minNum+1, 0, 0);
        if(sum==0) break;
    }


    // get_variables(lp, row);
    // for(int i = 0 ; i < NewNodeNum ; i++){
    //     visitOrder.push_back(row[NewNodeNum*NewNodeNum+i]);
    // }
    // int objective = get_objective(lp);
    
    // if(row[NewNodeNum*NewNodeNum+1] == 0){
    //     cout<<"无法寻找路径,可能的原因:\n1,起点与终点不连通\n2,输入错误\n";
    //     exit(1);
    // }
    
    // delete_lp(lp);
    // return objective;
}
vector<int> getliu(){
    
}

int main(int argc,char *argv[]){

    vector<vector<int> > img; 
    vector<int> firstLine; 
    readTopo(argv[1],img,firstLine);




    for(int i = 0 ; i <firstLine.size() ; i++){
        cout<<firstLine[i]<<endl;
    }
    for(int i = 0 ; i < img.size() ; i++){
        for(int j = 0 ; j < img[i].size() ; j++){
            cout<<img[i][j]<<'\t';
        }
        cout<<"\n";
    }

    XXGH(img);
}