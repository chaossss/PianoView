package com.github.chaossss.pianoview;

/**
 * Created by chaos on 2016/1/20.
 */
public class CardData {
    private CardData() {
    }

    public static Card getCard(int i) {
        Card card = new Card();
        switch (i) {
            case 0:
                card.setTitle("God of Light");
                card.setSubTitle("点亮世界之光");
                card.setDigest("当下制造精致的游戏往往超越了常规概念中对游戏的界定。通关的过程更像是在欣赏一部电影大片。通过镜片反射，即使只有一道光芒，我们也能点亮世界");
                card.setUpNum(124);
                card.setAuthorName("小美");
                card.setBackgroundColor("#00aac6");
                card.setCoverImgerUrl("card_cover1");
                card.setIconUrl("card_icon1");
                break;
            case 1:
                card.setTitle("我的手机与众不同");
                card.setSubTitle("专题");
                card.setDigest("谁说美化一定要Root?选对了应用一样可以美美哒～有个性，爱折腾，我们不爱啃苹果，我们是大安卓用户!都说「世界上没有相同的叶子」，想让自己的手机与众不同?让小美告诉你");
                card.setUpNum(299);
                card.setAuthorName("小美");
                card.setBackgroundColor("#dc4e97");
                card.setCoverImgerUrl("card_cover2");
                card.setIconUrl("card_icon2");
                break;
            case 2:
                card.setTitle("BlackLight");
                card.setSubTitle("做最纯粹的微博客户端");
                card.setDigest("Android的官方微博客户端显得太过臃肿，这让不少人转而投向第三方客户端。「Fuubo」、「四次元」、「Smooth」，一个个耳熟能详的名字，它们各有千秋，也吸引了一大票追随者，而今天推荐的BlackLight，又是一个被重复造出的「轮子」，然而这个后来者可不一般");
                card.setUpNum(241);
                card.setAuthorName("小最");
                card.setBackgroundColor("#00aac6");
                card.setCoverImgerUrl("card_cover3");
                card.setIconUrl("card_icon3");
                break;
            case 3:
                card.setTitle("BuzzFeed");
                card.setSubTitle("最好玩的新闻在这里");
                card.setDigest("BuzzFeed是一款聚合新闻阅读应用，这款应用来自美国用户增长流量最快，内容最能吸引大众眼球的互联网新闻网站，当然我们不必知道BuzzFeed的创始人多么流弊，BuzzFeed本身是多么具有颠覆性，我们只需要知道这款应用的内容绝对有料，而去也是十分精致，简洁");
                card.setUpNum(119);
                card.setAuthorName("小最");
                card.setBackgroundColor("#e76153");
                card.setCoverImgerUrl("card_cover4");
                card.setIconUrl("card_icon4");
                break;
            case 4:
                card.setTitle("Nester");
                card.setSubTitle("专治各种熊孩子");
                card.setDigest("Nester简单的说是一款用于家长限制孩子玩手机的应用，这只可爱的圆滚滚的小鸟不仅可以设置孩子可以使用的应用，还可以用定时器控释孩子玩手机的时长。在小最看来，Nester最直白的描述就是专治各种熊孩子");
                card.setUpNum(97);
                card.setAuthorName("小最");
                card.setBackgroundColor("#9a6dbb");
                card.setCoverImgerUrl("card_cover5");
                card.setIconUrl("card_icon5");
                break;
            case 5:
                card.setTitle("二次元专题");
                card.setSubTitle("啊喂，别总想去四维空间啦");
                card.setDigest("为了满足美友中不少二次元少年的需求，小最前几日特(bei)意(po)被拍扁为二维状，去那个神奇的世界走了一遭。在被深深震撼之后，为大家带来本次「二次元专题」");
                card.setUpNum(317);
                card.setAuthorName("小最");
                card.setBackgroundColor("#51aa53");
                card.setCoverImgerUrl("card_cover6");
                card.setIconUrl("card_icon6");
                break;
            case 6:
                card.setTitle("Music Player");
                card.setSubTitle("闻其名，余音绕梁");
                card.setDigest("一款App，纯粹到极致，便是回到原点「Music Player」，一款音乐播放器，一个干净到显得敷衍的名字。它所打动的，是哪些需要音乐，才可以慰借心灵的人。");
                card.setUpNum(385);
                card.setAuthorName("小最");
                card.setBackgroundColor("#ea5272");
                card.setCoverImgerUrl("card_cover7");
                card.setIconUrl("card_icon7");
                break;
            case 7:
                card.setTitle("el");
                card.setSubTitle("剪纸人の唯美旅程");
                card.setDigest("断崖之上，孤牢中醒来的他，意外地得到一把能乘风翱翔的伞，于是在悠扬的钢琴曲中，剪纸人开始了漫无目的的漂泊之旅。脚下的重峦叠嶂，飞行中遇到的种种障碍，不日又遇到了他，将会有一段怎样的旅程?");
                card.setUpNum(622);
                card.setAuthorName("小美");
                card.setBackgroundColor("#e76153");
                card.setCoverImgerUrl("card_cover8");
                card.setIconUrl("card_icon8");
                break;
        }
        return card;
    }
}
