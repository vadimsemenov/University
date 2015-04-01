CAT_DIR = cat/
REVWORDS_DIR = revwords/
FILTER_DIR = filter/
BUFCAT_DIR = bufcat/

all: cat/cat revwords/revwords filter/filter bufcat/bufcat

cat/cat: 
	$(MAKE) -C $(CAT_DIR)

revwords/revwords:
	$(MAKE) -C $(REVWORDS_DIR)

filter/filter:
	$(MAKE) -C $(FILTER_DIR)

bufcat/bufcat:
	$(MAKE) -C $(BUFCAT_DIR)

clean:
	$(MAKE) clean -C $(CAT_DIR)
	$(MAKE) clean -C $(REVWORDS_DIR)
	$(MAKE) clean -C $(FILTER_DIR)

