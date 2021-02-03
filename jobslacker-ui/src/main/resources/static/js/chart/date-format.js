function   DateAdd(interval,number,date)   
{   
/*   
  *---------------   DateAdd(interval,number,date)   -----------------   
  *   DateAdd(interval,number,date)     
  *   功能:实现VBScript的DateAdd功能.   
  *   参数:interval,字符串表达式，表示要添加的时间间隔.   
  *   参数:number,数值表达式，表示要添加的时间间隔的个数.   
  *   参数:date,时间对象.   
  *   返回:新的时间对象.   
  *   var   now   =   new   Date();   
  *   var   newDate   =   DateAdd("d",5,now);   
  *   author:wanghr100(灰豆宝宝.net)   
  *   update:2004-5-28   11:46   
  *---------------   DateAdd(interval,number,date)   -----------------   
  */   
        switch(interval)   
        {   
                case   "y"   :   {   
                        date.setFullYear(date.getFullYear()+number);   
                        return   date;   
                        break;   
                }   
                case   "q"   :   {   
                        date.setMonth(date.getMonth()+number*3);   
                        return   date;   
                        break;   
                }   
                case   "m"   :   {   
                        date.setMonth(date.getMonth()+number);
                        return   date;   
                        break;   
                }   
                case   "w"   :   {   
                        date.setDate(date.getDate()+number*7);   
                        return   date;   
                        break;   
                }   
                case   "d"   :   {   
                        date.setDate(date.getDate()+number);   
                        return   date;   
                        break;   
                }   
                case   "hh"   :   {   
                        date.setHours(date.getHours()+number);   
                        return   date;   
                        break;   
                }   
                case   "mm"   :   {   
                        date.setMinutes(date.getMinutes()+number);   
                        return   date;   
                        break;   
                }   
                case   "ss"   :   {   
                        date.setSeconds(date.getSeconds()+number);   
                        return   date;   
                        break;   
                }   
                default   :   {   
                        date.setDate(d.getDate()+number);   
                        return   date;   
                        break;   
                }   
        }   
}   
function   showWeekFirstDay(inputDate)   
{   
var   WeekFirstDay=new   Date(inputDate-(inputDate.getDay()-1)*86400000);   
return  WeekFirstDay;
}   
function   showWeekLastDay(inputDate)   
{   
var   Nowdate=new   Date(inputDate);   
var   WeekFirstDay=new   Date(inputDate-(inputDate.getDay()-1)*86400000);   
var   WeekLastDay=new   Date((WeekFirstDay/1000+6*86400)*1000);   
return  WeekLastDay;
}   

function showWeekLastBusinessDay(inputDate){
	var   Nowdate=new   Date(inputDate);   
	var   WeekFirstDay=new   Date(inputDate-(inputDate.getDay()-1)*86400000);   
	var   WeekLastDay=new   Date((WeekFirstDay/1000+4*86400)*1000);   
	return  WeekLastDay;
}
function   showMonthFirstDay(inputDate)   
{   
var   MonthFirstDay=new   Date(inputDate.getFullYear(),inputDate.getMonth(),1);   
return MonthFirstDay;   
}   
function   showMonthLastDay(inputDate)   
{   
var   MonthNextFirstDay=new   Date(inputDate.getFullYear(),inputDate.getMonth()+1,1);   
var   MonthLastDay=new   Date(MonthNextFirstDay-86400000);   
return MonthLastDay;   
}


function DateFormat(pattern, formatSymbols) 
{ 
   if(pattern == null || pattern == undefined) 
   { 
       pattern = "yyyy-MM-dd HH:mm:ss SSS"; 
   } 
 
   if(formatSymbols == null || formatSymbols == undefined) 
   { 
       formatSymbols = "yMdHmsS"; 
   } 
 
   this.pattern = pattern; 
   this.formatSymbols = formatSymbols; 
} 
 
DateFormat.prototype.format = function(date) 
{ 
   var time = getTime(date); 
     
   // 标记存入数组 
   var cs = this.formatSymbols.split(""); 
 
   // 格式存入数组 
   var fs = this.pattern.split(""); 
 
   // 构造数组 
   var ds = time.split(""); 
 
   // 标志年月日的结束下标 
   var y = 3; 
   var M = 6; 
   var d = 9; 
   var H = 12; 
   var m = 15; 
   var s = 18; 
   var S = 22; 
 
   // 逐位替换年月日时分秒和毫秒 
   for(var i = fs.length - 1; i > -1; i--) 
   { 
       switch (fs[i]) 
       { 
           case cs[0]: 
           { 
               fs[i] = ds[y--]; 
               break; 
           } 
           case cs[1]: 
           { 
               fs[i] = ds[M--]; 
               break; 
           } 
           case cs[2]: 
           { 
               fs[i] = ds[d--]; 
               break; 
           } 
           case cs[3]: 
           { 
               fs[i] = ds[H--]; 
               break; 
           } 
           case cs[4]: 
           { 
               fs[i] = ds[m--]; 
               break; 
           } 
           case cs[5]: 
           { 
               fs[i] = ds[s--]; 
               break; 
           } 
           case cs[6]: 
           { 
               fs[i] = ds[S--]; 
               break; 
           } 
       } 
   } 
 
   return fs.join(""); 
} 
 
/** 
*@param datetime - String 
* 
*@return - Date 
*/
DateFormat.prototype.parse = function(date) 
{ 
   var y = ""; 
   var M = ""; 
   var d = ""; 
   var H = ""; 
   var m = ""; 
   var s = ""; 
   var S = ""; 
 
   // 标记存入数组 
   var cs = this.formatSymbols.split(""); 
 
   // 格式存入数组 
   var ds = this.pattern.split(""); 
 
   // date   = "2005-08-22 12:12:12 888"; 
   // format = "yyyy-MM-dd HH:mm:ss SSS"; 
   // sign   = "yMdHmsS"; 
   var size = Math.min(ds.length, date.length); 
 
   for(var i=0; i<size; i++) 
   { 
       switch (ds[i]) 
       { 
           case cs[0]: 
           { 
               y += date.charAt(i); 
               break; 
           } 
           case cs[1]: 
           { 
               M += date.charAt(i); 
               break; 
           } 
           case cs[2]: 
           { 
               d += date.charAt(i); 
               break; 
           } 
           case cs[3]: 
           { 
               H += date.charAt(i); 
               break; 
           } 
           case cs[4]: 
           { 
               m += date.charAt(i); 
               break; 
           } 
           case cs[5]: 
           { 
               s += date.charAt(i); 
               break; 
           } 
           case cs[6]: 
           { 
               S += date.charAt(i); 
               break; 
           } 
       } 
   } 
     
   if(y.length < 1) y = 0; else y = parseInt(y); 
   if(M.length < 1) M = 0; else M = parseInt(M); 
   if(d.length < 1) d = 0; else d = parseInt(d); 
   if(H.length < 1) H = 0; else H = parseInt(H); 
   if(m.length < 1) m = 0; else m = parseInt(m); 
   if(s.length < 1) s = 0; else s = parseInt(s); 
   if(S.length < 1) S = 0; else S = parseInt(S); 
 
   var d = new Date(y, M - 1, d, H, m, s, S); 
 
   return d; 
} 
 
// 返回当前时间 
function getTime(date) 
{ 
   if(date == null) 
   { 
       date = new Date(); 
   } 
     
   var y = date.getFullYear(); 
   var M = date.getMonth() + 1; 
   var d = date.getDate(); 
   var h = date.getHours(); 
   var m = date.getMinutes(); 
   var s = date.getSeconds(); 
   var S = date.getTime()%1000; 
 
   var html = y + "-"; 
 
   if(M < 10) 
   { 
       html += "0"; 
   } 
   html += M + "-"; 
 
   if(d < 10) 
   { 
       html += "0"; 
   } 
   html += d + " "; 
 
   if(h < 10) 
   { 
       html += "0"; 
   } 
   html += h + ":"; 
 
   if(m < 10) 
   { 
       html += "0"; 
   } 
   html += m + ":"; 
 
   if(s < 10) 
   { 
       html += "0"; 
   } 
   html += s; 
     
   html += " "; 
 
   if(S < 100) 
   { 
       html += "0"
   } 
 
   if(S < 10) 
   { 
       html += "0"; 
   } 
 
   html += S; 
 
   return html; 
}

