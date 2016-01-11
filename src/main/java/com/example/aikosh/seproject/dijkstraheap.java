package com.example.aikosh.seproject;

class dijkstraheap {

        public dijkstraheap()
        {

        }
        /*==========================================================
            Array format: a[i][0] - vertex
              i>=1		  a[i][1] - number of edges from vertex
                          a[i][2,3,..] - edges to other vertices
        ===========================================================*/
        static int[][] a;

        private void swap(int x,int y)
        {
                int z=h[x];
                h[x]=h[y];
                h[y]=z;
                p[h[x]]=x;
                p[h[y]]=y;
        }
        static int n;
        final private double inf=Integer.MAX_VALUE+.0;
        private int c;
        private double[] d=new double[n+1];
        private int[] h=new int[n+1];
        static int[] num;
        static int[] p;
        static int[] par;
        private int[] path=new int[n+1];

        private void up(int x)
        {
                while(x>1)
                        if (d[h[x]]<d[h[x/2]])
                        {
                                swap(x,x/2);
                                x/=2;
                        } else break;
        }

        public int[] getPath(int start,int finish)
        {
                for(int i=1;i<=n;i++)
                {
                        d[i]=inf;
                        p[a[i][0]]=0;
                        par[a[i][0]]=0;
                        num[a[i][0]]=i;
                }
                c=1;
                d[num[start]]=0.0;
                h[1]=num[start];
                par[start]=-1;
                while(c>0 && h[1]!=num[finish])
                {
                        for(int j=2;j<=a[h[1]][1]+1;j++)
                        {
                                if (d[num[a[h[1]][j]]]==inf)
                                {
                                        d[num[a[h[1]][j]]]=d[h[1]]+GesturesView.w[h[1]][j-2];
                                        c++;
                                        h[c]=num[a[h[1]][j]];
                                        p[num[a[h[1]][j]]]=c;
                                        par[a[h[1]][j]]=a[h[1]][0];
                                        up(c);
                                } else
                                if (d[h[1]]+GesturesView.w[h[1]][j-2]<d[num[a[h[1]][j]]])
                                {
                                        d[num[a[h[1]][j]]]=d[h[1]]+GesturesView.w[h[1]][j-2];
                                        par[a[h[1]][j]]=a[h[1]][0];
                                        up(p[num[a[h[1]][j]]]);
                                }
                        }
                        h[1]=h[c];
                        c--;
                        int x=1;
                        while (x<c)
                        {
                                int x1=x;
                                if (c>=x*2 && d[h[x]]>d[h[x*2]]) x1=x*2;
                                if (c>=x*2+1 && d[h[x1]]>d[h[x*2+1]]) x1=x*2+1;
                                if (x!=x1)
                                {
                                        swap(x,x1);
                                        x=x1;
                                } else break;
                        }
                }
                if (d[num[finish]]!=inf)
                {
                        //System.out.printf("%.2f\n",d[num[finish]]);
                        //System.out.println(d[num[finish]]);
                        int x=finish;
                        c=0;
                        while(x!=-1)
                        {
                                c++;
                                path[c]=x;
                                x=par[x];
                        }
                        for(int i=1;i<=c/2;i++)
                        {
                                x=path[i];
                                path[i]=path[c-i+1];
                                path[c-i+1]=x;
                        }
                        //for(int i=1;i<=c;i++) System.out.print(path[i]+" ");
                        return path;
                }// else System.out.println("The destination point is unreachable");
                return null;
        }
        public double getDist(int start,int finish)
        {
                for(int i=1;i<=n;i++)
                {
                        d[i]=inf;
                        p[a[i][0]]=0;
                        num[a[i][0]]=i;
                }
                c=1;
                d[num[start]]=0.0;
                h[1]=num[start];
                while(c>0 && h[1]!=num[finish])
                {
                        for(int j=2;j<=a[h[1]][1]+1;j++)
                        {
                                if (d[num[a[h[1]][j]]]==inf)
                                {
                                        d[num[a[h[1]][j]]]=d[h[1]]+GesturesView.w[h[1]][j-2];
                                        c++;
                                        h[c]=num[a[h[1]][j]];
                                        p[num[a[h[1]][j]]]=c;
                                        up(c);
                                } else
                                if (d[h[1]]+GesturesView.w[h[1]][j-2]<d[num[a[h[1]][j]]])
                                {
                                        d[num[a[h[1]][j]]]=d[h[1]]+GesturesView.w[h[1]][j-2];
                                        up(p[num[a[h[1]][j]]]);
                                }
                        }
                        h[1]=h[c];
                        c--;
                        int x=1;
                        while (x<c)
                        {
                                int x1=x;
                                if (c>=x*2 && d[h[x]]>d[h[x*2]]) x1=x*2;
                                if (c>=x*2+1 && d[h[x1]]>d[h[x*2+1]]) x1=x*2+1;
                                if (x!=x1)
                                {
                                        swap(x,x1);
                                        x=x1;
                                } else break;
                        }
                }
                if (d[num[finish]]!=inf) return d[num[finish]];
                // else System.out.println("The destination point is unreachable");
                return -1;
        }
}