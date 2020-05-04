CC = g++
DBG = debug
REL = release
DEP_FILE = Makefile.d

CFLAGS_DBG = -g -DDEBUG -D_DEBUG
CFLAGS_REL = -O2 -DNDEBUG

_CFLAGS = -Wall -std=c++11 -Iinclude
CFLAGS_SRC = $(_CFLAGS) -fPIC -D_TPMCPPLIB
CFLAGS_SMPL = $(_CFLAGS)

SRCDIR = Src
SMPLDIR = Samples
BINDIR = bin

TSS_A = $(BINDIR)/tss$(SUFF).a
TSS_SO = $(BINDIR)/tss$(SUFF).so
SAMPLES = $(BINDIR)/samples

OBJDIR = $(BINDIR)/obj
OBJ_SRC_DIR = $(OBJDIR)/$(SRCDIR)/$(CONFIG)
OBJ_SMPL_DIR = $(OBJDIR)/$(SMPLDIR)/$(CONFIG)

DEP = $(OBJ_SRC_DIR)/$(DEP_FILE)
DEP_SMPL = $(OBJ_SMPL_DIR)/$(DEP_FILE)

SRCS := $(wildcard $(SRCDIR)/*.cpp)
OBJS = $(addprefix $(OBJDIR)/,$(SRCS:$(SRCDIR)/%.cpp=$(SRCDIR)/$(CONFIG)/%.o))

SMPL_SRCS := $(wildcard $(SMPLDIR)/*.cpp)
SMPL_OBJS = $(addprefix $(OBJDIR)/,$(SMPL_SRCS:$(SMPLDIR)/%.cpp=$(SMPLDIR)/$(CONFIG)/%.o))


CONFIG := $(or $(CONFIG),$(config),$(CFG),$(cfg))

ifeq (,$(filter $(CONFIG),debug release))
  ifdef CONFIG
    $(error If specified, CONFIG can only be {debug|release})
  endif
  CONFIG = debug
endif


ifeq ($(CONFIG),debug)
  CFLAGS_SRC += $(CFLAGS_DBG)
  CFLAGS_SMPL += $(CFLAGS_DBG)
  SUFF = d
else
  CFLAGS_SRC += $(CFLAGS_REL)
  CFLAGS_SMPL += $(CFLAGS_REL)
endif


all: tss samples


DEPDIR := $(OBJ_SRC_DIR)
DEPFLAGS = -MT $@ -MMD -MP -MF $(DEPDIR)/$*.d

$(OBJ_SRC_DIR)/%.o: $(SRCDIR)/%.cpp $(DEPDIR)/%.d | $(DEPDIR)
	$(CC) -c $< -o $@ $(CFLAGS_SRC) $(DEPFLAGS)

$(DEPDIR): ; @mkdir -p $@

DEPFILES := $(OBJS:%.o=%.d)
$(DEPFILES):

-include $(DEPFILES)


$(TSS_A): $(OBJS)
	ar -crs $(TSS_A) $(OBJS)
	
$(TSS_SO): $(OBJS)
	$(CC) -o $(TSS_SO) $(OBJS) -shared

tss: $(TSS_SO)


DEPDIR_SMPL := $(OBJ_SMPL_DIR)
DEPFLAGS_SMPL = -MT $@ -MMD -MP -MF $(DEPDIR_SMPL)/$*.d

$(OBJ_SMPL_DIR)/%.o: $(SMPLDIR)/%.cpp $(DEPDIR_SMPL)/%.d | $(DEPDIR_SMPL)
	$(CC) -c $< -o $@ $(CFLAGS_SMPL) $(DEPFLAGS_SMPL)

$(DEPDIR_SMPL): ; @mkdir -p $@

DEPFILES_SMPL := $(SMPL_OBJS:%.o=%.d)
$(DEPFILES_SMPL):

-include $(DEPFILES_SMPL)


$(SAMPLES): $(SMPL_OBJS) $(TSS_A)
	$(CC) -o $(SAMPLES) $(SMPL_OBJS) $(TSS_A) $(CFLAGS_SMPL) -L/usr/lib -lcrypto

samples: $(SAMPLES)

test: $(SAMPLES)
	$(SAMPLES)
	
list:
	@echo Targets: {clean|all|tss|samples}
	@echo Build configurations: {CONFIG|config|CFG|cfg} = {debug|release}
	
help: list
	@echo Default: all debug

clean:
	@rm -rf $(OBJDIR)
	@rm -f $(SAMPLES) $(TSS_A) $(TSS_SO)


.PHONY: clean help list all tss test
