//下列函数都会自动获得浏览器调用，无需用户手动处理
$package('loon.main');

/**
 * loon入口函数
 */
function OnMain(){
	//设置游戏初始化参数
    var setting = new Setting();
        setting.fps = 30;
	    setting.showFps = true;
		//预加载文件(预加载会一直等待加载完毕，如果某个文件无法读取，则会停留在初始界面不动)
	    setting.preload = ['pad.png','map1.gif', 'chara.png'];

        //关闭手动API渲染接口
		config.useCanvasUpdate = false;

       //注册游戏设置
       register(setting);
};


/**
 * 游戏主进程，游戏内容在这里获得初始化执行
 */
function OnProcess(/*loon.Core*/core){		

	var map = new loon.Map(16, 16);
        map.image = core.file['map1.gif'];
        map.loadData([
            [322,322,322,322,322,322,224,225,225,225,225,225,167,205,205,205,205,205,205,205,205,205,205,205,205,205,205,205,205,205],
            [322,322,322,322,322,322,322,322,322,322,322,322,224,225,225,225,225,225,167,205,205,205,205,205,205,205,205,205,205,205],
            [322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,224,225,225,225,225,225,225,225,225,225,225,225],
            [322,322,322,342,342,342,342,342,342,342,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,342,342,342,342,342,342,342,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,342,342,342,342,342,342,342,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,342,342,342,342,342,342,342,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,342,342,342,342,342,342,342,322,322,322,322,322,322,322,342,342,342,342,342,342,342,342,342,322,322,322,322],
            [322,322,322,342,342,342,342,342,342,342,322,322,322,322,322,322,322,342,342,342,342,342,342,342,342,342,322,322,322,322],
            [322,322,322,342,342,342,341,341,341,342,322,322,322,322,322,322,322,342,342,342,342,342,342,342,342,342,322,322,322,322],
            [322,322,322, 24, 25, 25, 25, 26,322,322,322,322,322,322,322,322,322,342,342,342,342,342,342,342,342,342,322,322,322,322],
            [322,322,322, 44, 45, 45, 45, 46,322,322,322,322,322,322,322,322,322,342,342,342,342,342,342,342,342,342,322,322,322,322],
            [322,322,322, 64,  7,  6, 65, 66,322,322,322,322,322,322,322,322,322,342,342,342,342,342,342,342,342,342,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,342,342,342,342,322,322,322,322,322,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,342,342,342,342,322,322,322,322,322,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,342,342,342,342,322,322,322,322,322,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,322, 24, 26,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322],
            [322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322],
            [ 25, 25, 25, 25,  5, 46,322,322,322,322,322,322,322,322,322,322,322,322, 44, 46,322,322,322,322,322,322,322,322,322,322],
            [ 45, 45, 45, 45, 45,  4, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,  5, 49,322,322,322,322,322,322,322,322,322,322],
            [ 45, 45, 45, 45, 45, 45,  6, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 66,322,322,322,322,322,322,322,322,322,322],
            [ 45, 45, 45, 45, 45, 45, 46,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322],
            [ 45, 45, 45, 45, 45,  6, 66,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,184,185,185,186,322,322,322],
            [ 65, 65, 65, 65, 65, 66,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,184,165,205,205,164,186,322,322],
            [322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,204,205,205,205,205,164,185,185],
            [322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,180,161,161,161,207,205,205,205,205,205,205,205],
            [322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,203,322,322,322,204,205,205,205,205,205,205,205],
            [322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,203,322,322,322,204,205,205,205,205,205,205,205]
        ],[
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,461,462, -1,461,462, -1,461,462,421,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,481,482, -1,481,482,421,481,482,421,481,482, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,421,421,321,341,341,341,341,341,321, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,461,462,321,422, -1, -1,400,400,321,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,481,482,321, -1, -1, -1, -1,400,321,481,482, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1,321,521,521,521,521,521,321,421, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,461,462,321, -1, -1, -1, -1, -1,321,461,462, -1, -1, -1, -1, -1,321,341,341,341,341,341,341,341,321, -1, -1, -1, -1],
            [ -1,481,482,321, -1, -1, -1, -1,400,321,481,482, -1, -1, -1, -1, -1,321,420, -1, -1, -1, -1,400,400,321, -1,421, -1, -1],
            [ -1, -1, -1,341, -1, -1, -1, -1, -1,341,421, -1, -1, -1, -1, -1, -1,321, -1, -1, -1, -1, -1, -1,400,321,461,462, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1,421,421, -1, -1, -1, -1, -1, -1, -1,321, -1, -1,321, -1, -1, -1, -1,321,481,482, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,321,521,521,321,402, -1, -1, -1,321, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462,321, -1, -1,321,341,341,341, -1,341, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,481,482,321, -1, -1,321, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,421,321, -1, -1,321, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,341, -1, -1,341, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,481,482,481,482, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,421, -1, -1, -1, -1,460, -1, -1, -1, -1, -1, -1, -1,461,462, -1,421, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,480, -1, -1, -1, -1, -1, -1, -1,481,482,421, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,481,482, -1, -1, -1, -1, -1, -1, -1,421, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,421,420, -1, -1, -1, -1, -1, -1, -1, -1]
        ]);
        map.collisionData = [
            [  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1],
            [  0,  1,  1,  0,  1,  1,  0,  1,  1,  0,  1,  1,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1],
            [  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  1,  0,  0,  0,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  1,  1,  1,  0,  0,  0,  0,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  1,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0],
            [  0,  1,  1,  1,  0,  0,  0,  0,  1,  1,  1,  1,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0,  0,  1,  1,  1,  0,  0,  0,  0],
            [  0,  0,  0,  1,  0,  0,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  0,  0,  0,  0,  1,  1,  1,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  0,  0,  0,  0,  1,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  1,  1,  0,  1,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  0,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  0,  0],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1],
            [  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1]
        ];

        var foregroundMap = new loon.Map(16, 16);
        foregroundMap.image = core.file['map1.gif'];
        foregroundMap.loadData([
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,461,462, -1,461,462, -1,461,462, -1,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,461,462, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1,461,462, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,402, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,461,462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
            [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1]
        ]);

        var player = new loon.Sprite(32, 32);
        player.x = 6 * 16 - 8;
        player.y = 10 * 16;
        var image = new loon.Bitmap(96, 128);
        image.draw(core.file['chara.png'], 0, 0);
        player.image = image;
        player.isMoving = false;
        player.direction = 0;
        player.walk = 1;
        player.addEventListener('enterframe', function() {
            this.frame = this.direction * 3 + this.walk;
            if (this.isMoving) {
                this.moveBy(this.vx, this.vy);
                if (!(core.frame % 3)) {
                    this.walk++;
                    this.walk %= 3;
                }
                if ((this.vx && (this.x-8) % 16 == 0) || (this.vy && this.y % 16 == 0)) {
                    this.isMoving = false;
                    this.walk = 1;
                }
            } else {
                this.vx = this.vy = 0;
                if (core.input.left) {
                    this.direction = 1;
                    this.vx = -4;
                } else if (core.input.right) {
                    this.direction = 2;
                    this.vx = 4;
                } else if (core.input.up) {
                    this.direction = 3;
                    this.vy = -4;
                } else if (core.input.down) {
                    this.direction = 0;
                    this.vy = 4;
                }
                if (this.vx || this.vy) {
                    var x = this.x + (this.vx ? this.vx / Math.abs(this.vx) * 16 : 0) + 16;
                    var y = this.y + (this.vy ? this.vy / Math.abs(this.vy) * 16 : 0) + 16;
                    if (0 <= x && x < map.width && 0 <= y && y < map.height && !map.hit(x, y)) {
                        this.isMoving = true;
						 arguments.callee.call(this);
                    }
                }
            }
        });

        var stage = new loon.Group();
        stage.addChild(map);
        stage.addChild(player);
        stage.addChild(foregroundMap);
        core.root.addChild(stage);


		var pad = new loon.Pad('pad.png');
        pad.x = 0;
        pad.y = 220;
        core.root.addChild(pad);

        core.root.addEventListener('enterframe', function(e) {
            var x = Math.min((core.width  - 16) / 2 - player.x, 0);
            var y = Math.min((core.height - 16) / 2 - player.y, 0);
            x = Math.max(core.width,  x + map.width)  - map.width;
            y = Math.max(core.height, y + map.height) - map.height;
            stage.x = x;
            stage.y = y;
        });
}

/**
 * 游戏画布刷新时将调用此函数(手动API渲染用,如果useCanvasUpdate为false则无效)
 */
//function OnUpdate(/*loon.Renderer*/render,/*float*/elapsed){
      //   render.drawText("loon-min-0.3.3",66,66);
//}
